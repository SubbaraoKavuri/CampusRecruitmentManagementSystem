package com.campusrecruit.security;

import com.campusrecruit.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
public class CampusUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CampusUserDetailsService(UserRepository users){this.users=users;}
    @Override public UserDetails loadUserByUsername(String email) {
        var user=users.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(()->new UsernameNotFoundException("Account not found"));
        if(!user.isEmailVerified()) throw new UsernameNotFoundException("Email is not verified");
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));
    }
}
