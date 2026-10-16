package com.campusrecruit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications", uniqueConstraints = @UniqueConstraint(name = "uk_application_job_student", columnNames = {"job_id", "student_id"}))
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "job_id", nullable = false) private JobPosting job;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id", nullable = false) private User student;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private ApplicationStatus status = ApplicationStatus.SUBMITTED;
    @Column(length = 3000) private String coverLetter;
    @CreationTimestamp @Column(nullable = false, updatable = false) private LocalDateTime appliedAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
    protected JobApplication() {}
    public JobApplication(JobPosting job, User student, String coverLetter){this.job=job;this.student=student;this.coverLetter=coverLetter;}
    public Long getId(){return id;} public JobPosting getJob(){return job;} public User getStudent(){return student;}
    public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){status=v;}
    public String getCoverLetter(){return coverLetter;} public void setCoverLetter(String v){coverLetter=v;}
    public LocalDateTime getAppliedAt(){return appliedAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
