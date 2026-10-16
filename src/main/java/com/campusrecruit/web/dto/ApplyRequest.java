package com.campusrecruit.web.dto;
import jakarta.validation.constraints.Size;
public record ApplyRequest(@Size(max=3000) String coverLetter) {}
