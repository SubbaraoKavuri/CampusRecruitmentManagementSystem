package com.campusrecruit.web;

import com.campusrecruit.service.AuthService;
import com.campusrecruit.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}
    @PostMapping("/register/request-code") public OtpChallengeResponse register(@Valid @RequestBody RegisterRequest r){return auth.requestRegistration(r);}
    @PostMapping("/register/verify-code") public AuthResponse verifyRegister(@Valid @RequestBody OtpRequest r){return auth.verifyRegistration(r);}
    @PostMapping("/login") public OtpChallengeResponse login(@Valid @RequestBody LoginRequest r){return auth.requestLogin(r);}
    @PostMapping("/resend-code") public OtpChallengeResponse resend(@Valid @RequestBody OtpResendRequest r){return auth.resend(r);}
    @PostMapping("/login/verify-code") public AuthResponse verifyLogin(@Valid @RequestBody OtpRequest r){return auth.verifyLogin(r);}
    @PostMapping("/password/request-code") public OtpChallengeResponse resetRequest(@RequestBody PasswordResetRequest r){return auth.requestPasswordReset(r.email());}
    @PostMapping("/password/reset") public void reset(@Valid @RequestBody PasswordResetRequest r){auth.resetPassword(r);}
    @GetMapping("/me") public AuthResponse.UserResponse me(Authentication a){return auth.me(a.getName());}
}
