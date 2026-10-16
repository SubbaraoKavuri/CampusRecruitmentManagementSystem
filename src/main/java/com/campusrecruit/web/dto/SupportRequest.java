package com.campusrecruit.web.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record SupportRequest(@NotBlank @Size(max=100) String name,@NotBlank @Email String email,
                             @NotBlank @Size(max=140) String subject,@NotBlank @Size(max=5000) String message) {}
