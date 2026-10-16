package com.campusrecruit.web.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
public record ProfileUpdateRequest(@Size(max=100) String name,@Size(max=100) String department,
                                   @Min(1) @Max(8) Integer yearOfStudy,@DecimalMin("0.0") @DecimalMax("4.0") Double cgpa,
                                   @Size(max=2000) String skills,@Size(max=30) String phone,@Size(max=500) String resumeUrl) {}
