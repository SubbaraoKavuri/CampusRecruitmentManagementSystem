package com.campusrecruit.web.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record OtpResendRequest(@NotBlank @Email String email,@NotBlank @Pattern(regexp="REGISTER|LOGIN|RESET") String purpose) {}
