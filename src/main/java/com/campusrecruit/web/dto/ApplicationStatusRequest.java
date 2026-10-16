package com.campusrecruit.web.dto;
import com.campusrecruit.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
public record ApplicationStatusRequest(@NotNull ApplicationStatus status) {}
