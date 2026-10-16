package com.campusrecruit.config;

import com.campusrecruit.security.JwtAuthenticationFilter;
import com.campusrecruit.security.CampusUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{return config.getAuthenticationManager();}
    @Bean DaoAuthenticationProvider authenticationProvider(CampusUserDetailsService users,PasswordEncoder encoder){
        var provider=new DaoAuthenticationProvider(); provider.setUserDetailsService(users); provider.setPasswordEncoder(encoder); return provider;
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter jwtFilter,DaoAuthenticationProvider provider)throws Exception{
        return http.csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/","/index.html","/app.css","/app.js","/favicon.ico","/api/auth/**","/api/support").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/support/contact").permitAll()
                        .requestMatchers("/swagger-ui/**","/v3/api-docs/**","/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errors->errors.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();
    }
}
