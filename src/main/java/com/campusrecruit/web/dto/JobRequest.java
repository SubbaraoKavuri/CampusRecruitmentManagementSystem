package com.campusrecruit.web.dto;
import com.campusrecruit.entity.JobSource;
import com.campusrecruit.entity.JobStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
public record JobRequest(@NotBlank @Size(max=160) String title,@NotBlank @Size(max=160) String companyName,
                         @NotBlank @Size(max=120) String location,@NotBlank @Size(max=12000) String description,
                         @Size(max=80) String employmentType,@Size(max=100) String salaryRange,
                         @NotNull JobSource source,@NotNull JobStatus status,
                         @NotNull @FutureOrPresent LocalDate applicationDeadline) {}
