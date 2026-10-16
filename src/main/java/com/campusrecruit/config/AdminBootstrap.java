package com.campusrecruit.config;

import com.campusrecruit.entity.Role;
import com.campusrecruit.entity.User;
import com.campusrecruit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users; private final PasswordEncoder encoder;
    @Value("${app.admin.email}") private String email;
    @Value("${app.admin.password}") private String password;
    @Value("${app.admin.name}") private String name;
    public AdminBootstrap(UserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
    @Override @Transactional public void run(ApplicationArguments args){
        String normalized=email.trim().toLowerCase();
        if(users.existsByEmail(normalized))return;
        User admin=new User();admin.setName(name);admin.setEmail(normalized);admin.setPassword(encoder.encode(password));admin.setRole(Role.ADMIN);admin.setEmailVerified(true);users.save(admin);
    }
}
