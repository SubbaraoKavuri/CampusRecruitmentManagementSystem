package com.campusrecruit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=100) private String name;
    @Column(nullable=false, length=254) private String email;
    @Column(nullable=false, length=140) private String subject;
    @Column(nullable=false, length=5000) private String message;
    @Column(nullable=false, length=20) private String status = "OPEN";
    @CreationTimestamp @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    protected SupportTicket() {}
    public SupportTicket(String name,String email,String subject,String message){this.name=name;this.email=email;this.subject=subject;this.message=message;}
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
    public String getSubject(){return subject;} public String getMessage(){return message;}
    public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
    public void setStatus(String status){this.status=status;}
}
