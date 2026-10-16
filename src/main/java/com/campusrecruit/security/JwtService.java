package com.campusrecruit.security;

import com.campusrecruit.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final byte[] secret;
    private final long expirationMs;
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration:86400000}") long expirationMs) {
        this.secret=secret.getBytes(StandardCharsets.UTF_8);
        if(this.secret.length<32) throw new IllegalArgumentException("JWT_SECRET must contain at least 32 bytes");
        if(expirationMs<=0) throw new IllegalArgumentException("JWT_EXPIRATION_MS must be positive");
        this.expirationMs=expirationMs;
    }
    public String issue(User user) {
        Date now=new Date();
        return Jwts.builder().setSubject(user.getEmail()).claim("role", user.getRole().name())
                .setIssuedAt(now).setExpiration(new Date(now.getTime()+expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256).compact();
    }
    public String subject(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret)).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public long expirationMs(){return expirationMs;}
}
