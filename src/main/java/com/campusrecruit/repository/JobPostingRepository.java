package com.campusrecruit.repository;

import com.campusrecruit.entity.JobPosting;
import com.campusrecruit.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findAllByStatusOrderByCreatedAtDesc(JobStatus status);
    List<JobPosting> findAllByStatusAndApplicationDeadlineGreaterThanEqualOrderByCreatedAtDesc(JobStatus status, LocalDate applicationDeadline);
    List<JobPosting> findAllByOrderByCreatedAtDesc();
}
