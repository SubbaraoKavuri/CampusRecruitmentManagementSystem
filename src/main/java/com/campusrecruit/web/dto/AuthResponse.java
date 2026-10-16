package com.campusrecruit.web.dto;
import com.campusrecruit.entity.Role;
public record AuthResponse(String tokenType,String accessToken,long expiresInSeconds,UserResponse user) {
    public record UserResponse(Long id,String name,String email,Role role,boolean emailVerified,String department){}
}
