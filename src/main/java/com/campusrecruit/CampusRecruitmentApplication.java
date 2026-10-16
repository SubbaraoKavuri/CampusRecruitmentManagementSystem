package com.campusrecruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point of the Campus Recruitment Management System backend.
 *
 * @author CampusRecruit Team
 */
@SpringBootApplication
@EnableAsync
public class CampusRecruitmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusRecruitmentApplication.class, args);
        System.out.println("Campus Recruitment Management System started on port 8080");
    }
}
