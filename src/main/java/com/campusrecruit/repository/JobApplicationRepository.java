package com.campusrecruit.repository;

import com.campusrecruit.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobIdAndStudentId(Long jobId, Long studentId);
    List<JobApplication> findAllByStudentIdOrderByAppliedAtDesc(Long studentId);
    List<JobApplication> findAllByOrderByAppliedAtDesc();
    long countByStatus(com.campusrecruit.entity.ApplicationStatus status);
    Optional<JobApplication> findByIdAndStudentId(Long id, Long studentId);
}
