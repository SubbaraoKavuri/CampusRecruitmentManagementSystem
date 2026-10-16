package com.campusrecruit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
public class JobPosting {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 160) private String companyName;
    @Column(nullable = false, length = 120) private String location;
    @Column(nullable = false, length = 12000) private String description;
    @Column(length = 80) private String employmentType;
    @Column(length = 100) private String salaryRange;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private JobSource source;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private JobStatus status = JobStatus.OPEN;
    @Column(nullable = false) private LocalDate applicationDeadline;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by", nullable = false) private User createdBy;
    @CreationTimestamp @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
    public JobPosting() {}
    public Long getId(){return id;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getEmploymentType(){return employmentType;} public void setEmploymentType(String v){employmentType=v;}
    public String getSalaryRange(){return salaryRange;} public void setSalaryRange(String v){salaryRange=v;}
    public JobSource getSource(){return source;} public void setSource(JobSource v){source=v;}
    public JobStatus getStatus(){return status;} public void setStatus(JobStatus v){status=v;}
    public LocalDate getApplicationDeadline(){return applicationDeadline;} public void setApplicationDeadline(LocalDate v){applicationDeadline=v;}
    public User getCreatedBy(){return createdBy;} public void setCreatedBy(User v){createdBy=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
