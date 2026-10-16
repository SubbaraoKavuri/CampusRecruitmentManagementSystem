package com.campusrecruit.repository;

import com.campusrecruit.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.rollNumber = :rollNumber")
    Optional<Student> findByRollNumber(@Param("rollNumber") String rollNumber);

    Optional<Student> findByUserEmail(String email);

    @Query("SELECT s FROM Student s WHERE s.department = :department")
    java.util.List<Student> findByDepartment(@Param("department") String department);
}
