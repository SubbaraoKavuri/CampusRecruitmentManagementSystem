package com.campusrecruit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps", indexes = @Index(name = "idx_otp_email_purpose", columnList = "email,purpose"))
public class EmailOtp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 254) private String email;
    @Column(nullable = false, length = 30) private String purpose;
    @Column(nullable = false, length = 64) private String codeHash;
    @Column(nullable = false) private LocalDateTime expiresAt;
    @Column(nullable = false) private int attempts;
    @Column(length = 100) private String pendingName;
    @Column(length = 100) private String pendingDepartment;
    @Column(length = 100) private String pendingPasswordHash;
    @CreationTimestamp @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    protected EmailOtp() {}
    public EmailOtp(String email, String purpose, String codeHash, LocalDateTime expiresAt, String name, String department, String passwordHash) {
        this.email=email; this.purpose=purpose; this.codeHash=codeHash; this.expiresAt=expiresAt;
        this.pendingName=name; this.pendingDepartment=department; this.pendingPasswordHash=passwordHash;
    }
    public Long getId(){return id;} public String getEmail(){return email;} public String getPurpose(){return purpose;}
    public String getCodeHash(){return codeHash;} public LocalDateTime getExpiresAt(){return expiresAt;}
    public int getAttempts(){return attempts;} public void setAttempts(int v){attempts=v;}
    public String getPendingName(){return pendingName;} public String getPendingDepartment(){return pendingDepartment;}
    public String getPendingPasswordHash(){return pendingPasswordHash;} public LocalDateTime getCreatedAt(){return createdAt;}
}
