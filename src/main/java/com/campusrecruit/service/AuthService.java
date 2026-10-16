package com.campusrecruit.service;

import com.campusrecruit.entity.*;
import com.campusrecruit.repository.*;
import com.campusrecruit.security.JwtService;
import com.campusrecruit.web.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class AuthService {
    private static final int OTP_MINUTES=10;
    private static final int MAX_ATTEMPTS=5;
    private final UserRepository users;
    private final StudentRepository students;
    private final EmailOtpRepository otps;
    private final PasswordEncoder passwords;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final EmailService email;
    private final SecureRandom random=new SecureRandom();
    @Value("${app.email.resend-cooldown-seconds:60}") private long resendCooldown;

    public AuthService(UserRepository users,StudentRepository students,EmailOtpRepository otps,PasswordEncoder passwords,
                       AuthenticationManager authenticationManager,JwtService jwt,EmailService email){
        this.users=users;this.students=students;this.otps=otps;this.passwords=passwords;
        this.authenticationManager=authenticationManager;this.jwt=jwt;this.email=email;
    }

    @Transactional
    public OtpChallengeResponse requestRegistration(RegisterRequest request){
        String address=normalize(request.email());
        if(users.existsByEmail(address)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already uses this email");
        return issue(address,"REGISTER",request.name().trim(),request.department(),passwords.encode(request.password()));
    }

    @Transactional(noRollbackFor=ResponseStatusException.class)
    public AuthResponse verifyRegistration(OtpRequest request){
        String address=normalize(request.email()); EmailOtp otp=consume(address,"REGISTER",request.code());
        if(users.existsByEmail(address)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already uses this email");
        User user=new User(); user.setName(otp.getPendingName()); user.setEmail(address);
        user.setPassword(otp.getPendingPasswordHash()); user.setRole(Role.STUDENT); user.setEmailVerified(true);
        users.saveAndFlush(user);
        Student student=new Student(); student.setUser(user); student.setDepartment(otp.getPendingDepartment()); students.save(student);
        otps.delete(otp);
        return response(user);
    }

    @Transactional
    public OtpChallengeResponse requestLogin(LoginRequest request){
        String address=normalize(request.email());
        try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(address,request.password())); }
        catch(AuthenticationException ex){ throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect"); }
        return issue(address,"LOGIN",null,null,null);
    }

    @Transactional
    public OtpChallengeResponse resend(OtpResendRequest request){
        String address=normalize(request.email()); String purpose=request.purpose();
        EmailOtp previous=otps.findTopByEmailAndPurposeOrderByCreatedAtDesc(address,purpose)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request a new verification code"));
        if(purpose.equals("REGISTER")&&users.existsByEmail(address))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already uses this email");
        if(purpose.equals("LOGIN")&&!users.existsByEmail(address))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request a new verification code");
        return issue(address,purpose,previous.getPendingName(),previous.getPendingDepartment(),previous.getPendingPasswordHash());
    }

    @Transactional(noRollbackFor=ResponseStatusException.class)
    public AuthResponse verifyLogin(OtpRequest request){
        String address=normalize(request.email()); EmailOtp otp=consume(address,"LOGIN",request.code()); otps.delete(otp);
        User user=users.findByEmail(address).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));
        return response(user);
    }

    @Transactional
    public OtpChallengeResponse requestPasswordReset(String emailAddress){
        String address=normalize(emailAddress);
        if(!users.existsByEmail(address)) return new OtpChallengeResponse("If the account exists, a reset code was sent.",600,"email_or_development",null);
        return issue(address,"RESET",null,null,null);
    }

    @Transactional(noRollbackFor=ResponseStatusException.class)
    public void resetPassword(PasswordResetRequest request){
        String address=normalize(request.email()); EmailOtp otp=consume(address,"RESET",request.code());
        User user=users.findByEmail(address).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));
        user.setPassword(passwords.encode(request.newPassword())); users.save(user); otps.delete(otp);
    }

    @Transactional(readOnly=true)
    public AuthResponse.UserResponse me(String emailAddress){
        User user=users.findByEmail(normalize(emailAddress)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        String department=students.findByUserEmail(user.getEmail()).map(Student::getDepartment).orElse(null);
        return new AuthResponse.UserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.isEmailVerified(),department);
    }

    private OtpChallengeResponse issue(String address,String purpose,String name,String department,String passwordHash){
        otps.findTopByEmailAndPurposeOrderByCreatedAtDesc(address,purpose).ifPresent(previous->{
            if(previous.getCreatedAt()!=null&&previous.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(resendCooldown)))
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Please wait before requesting another code");
        });
        otps.deleteAllByEmailAndPurpose(address,purpose);
        String code=String.format(Locale.ROOT,"%06d",random.nextInt(1_000_000));
        EmailOtp otp=new EmailOtp(address,purpose,hash(address,purpose,code),LocalDateTime.now().plusMinutes(OTP_MINUTES),name,department,passwordHash);
        otps.saveAndFlush(otp);
        boolean sent=email.sendOtp(address,code,purpose);
        return new OtpChallengeResponse(sent?"A verification code was sent to your email.":"Development code generated.",OTP_MINUTES*60L,
                sent?"email":"development",email.canShowDevelopmentCode()?code:null);
    }

    private EmailOtp consume(String address,String purpose,String code){
        EmailOtp otp=otps.findTopByEmailAndPurposeOrderByCreatedAtDesc(address,purpose)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request a new verification code"));
        if(otp.getExpiresAt().isBefore(LocalDateTime.now())){otps.delete(otp);throw new ResponseStatusException(HttpStatus.GONE,"Verification code expired; request a new one");}
        if(otp.getAttempts()>=MAX_ATTEMPTS){otps.delete(otp);throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Too many incorrect codes; request another");}
        if(!MessageDigest.isEqual(otp.getCodeHash().getBytes(StandardCharsets.US_ASCII),hash(address,purpose,code).getBytes(StandardCharsets.US_ASCII))){
            otp.setAttempts(otp.getAttempts()+1); otps.save(otp); throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Verification code is incorrect");
        }
        return otp;
    }
    private AuthResponse response(User user){
        String department=students.findByUserEmail(user.getEmail()).map(Student::getDepartment).orElse(null);
        return new AuthResponse("Bearer",jwt.issue(user),jwt.expirationMs(),new AuthResponse.UserResponse(user.getId(),user.getName(),user.getEmail(),user.getRole(),user.isEmailVerified(),department));
    }
    private static String normalize(String email){return email.trim().toLowerCase(Locale.ROOT);}
    private static String hash(String email,String purpose,String code){
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest((email+":"+purpose+":"+code).getBytes(StandardCharsets.UTF_8)));}
        catch(Exception ex){throw new IllegalStateException("SHA-256 is unavailable",ex);}
    }
}
