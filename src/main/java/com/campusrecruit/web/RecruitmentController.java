package com.campusrecruit.web;

import com.campusrecruit.entity.*;
import com.campusrecruit.repository.*;
import com.campusrecruit.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@RestController
@Transactional
public class RecruitmentController {
    private final JobPostingRepository jobs; private final JobApplicationRepository applications;
    private final UserRepository users; private final StudentRepository students; private final SupportTicketRepository tickets;
    public RecruitmentController(JobPostingRepository jobs,JobApplicationRepository applications,UserRepository users,StudentRepository students,SupportTicketRepository tickets){this.jobs=jobs;this.applications=applications;this.users=users;this.students=students;this.tickets=tickets;}

    @GetMapping("/api/jobs") @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public List<Map<String,Object>> jobList(Authentication a){return (isAdmin(a)?jobs.findAllByOrderByCreatedAtDesc():jobs.findAllByStatusAndApplicationDeadlineGreaterThanEqualOrderByCreatedAtDesc(JobStatus.OPEN,LocalDate.now())).stream().map(this::jobView).toList();}
    @PostMapping("/api/jobs") @PreAuthorize("hasRole('ADMIN')") public Map<String,Object> createJob(@Valid @RequestBody JobRequest r,Authentication a){JobPosting j=new JobPosting();copyJob(j,r);j.setCreatedBy(user(a));return jobView(jobs.save(j));}
    @PutMapping("/api/jobs/{id}") @PreAuthorize("hasRole('ADMIN')") public Map<String,Object> updateJob(@PathVariable Long id,@Valid @RequestBody JobRequest r){JobPosting j=jobs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));copyJob(j,r);return jobView(jobs.save(j));}
    @DeleteMapping("/api/jobs/{id}") @PreAuthorize("hasRole('ADMIN')") public void deleteJob(@PathVariable Long id){if(applications.findAllByOrderByAppliedAtDesc().stream().anyMatch(x->x.getJob().getId().equals(id)))throw new ResponseStatusException(HttpStatus.CONFLICT,"This job has applications; close it instead");jobs.deleteById(id);}
    @PostMapping("/api/jobs/{id}/apply") @PreAuthorize("hasRole('STUDENT')") public Map<String,Object> apply(@PathVariable Long id,@Valid @RequestBody(required=false) ApplyRequest r,Authentication a){
        JobPosting job=jobs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Job not found"));
        if(job.getStatus()!=JobStatus.OPEN||job.getApplicationDeadline().isBefore(LocalDate.now()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Applications are closed for this job");
        User student=user(a);if(applications.existsByJobIdAndStudentId(id,student.getId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already applied");
        JobApplication app=applications.save(new JobApplication(job,student,r==null?null:r.coverLetter()));return applicationView(app);
    }
    @GetMapping("/api/applications") @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')") public List<Map<String,Object>> applicationList(Authentication a){return (isAdmin(a)?applications.findAllByOrderByAppliedAtDesc():applications.findAllByStudentIdOrderByAppliedAtDesc(user(a).getId())).stream().map(this::applicationView).toList();}
    @PatchMapping("/api/applications/{id}/status") @PreAuthorize("hasRole('ADMIN')") public Map<String,Object> updateStatus(@PathVariable Long id,@Valid @RequestBody ApplicationStatusRequest r){JobApplication app=applications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));app.setStatus(r.status());return applicationView(applications.save(app));}
    @PatchMapping("/api/applications/{id}/withdraw") @PreAuthorize("hasRole('STUDENT')") public Map<String,Object> withdraw(@PathVariable Long id,Authentication a){JobApplication app=applications.findByIdAndStudentId(id,user(a).getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!List.of(ApplicationStatus.SUBMITTED,ApplicationStatus.UNDER_REVIEW).contains(app.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"This application can no longer be withdrawn");app.setStatus(ApplicationStatus.WITHDRAWN);return applicationView(applications.save(app));}
    @GetMapping("/api/admin/students") @PreAuthorize("hasRole('ADMIN')") public List<Map<String,Object>> studentList(){return students.findAll().stream().map(s->Map.<String,Object>of("id",s.getUser().getId(),"name",s.getUser().getName(),"email",s.getUser().getEmail(),"department",Objects.toString(s.getDepartment(),""),"yearOfStudy",Objects.toString(s.getYearOfStudy(),""),"cgpa",Objects.toString(s.getCgpa(),""),"skills",Objects.toString(s.getSkills(),""))).toList();}
    @GetMapping("/api/profile") @PreAuthorize("hasRole('STUDENT')") public Map<String,Object> profile(Authentication a){return profileView(student(a));}
    @PatchMapping("/api/profile") @PreAuthorize("hasRole('STUDENT')") public Map<String,Object> updateProfile(@RequestBody ProfileUpdateRequest r,Authentication a){Student s=student(a);User u=s.getUser();if(r.name()!=null&&!r.name().isBlank())u.setName(r.name().trim());if(r.department()!=null)s.setDepartment(r.department());if(r.yearOfStudy()!=null)s.setYearOfStudy(r.yearOfStudy());if(r.cgpa()!=null)s.setCgpa(r.cgpa());if(r.skills()!=null)s.setSkills(r.skills());if(r.phone()!=null)s.setPhone(r.phone());if(r.resumeUrl()!=null)s.setResumeUrl(r.resumeUrl());users.save(u);return profileView(students.save(s));}
    @GetMapping("/api/dashboard") @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')") public Map<String,Object> dashboard(Authentication a){if(isAdmin(a))return Map.of("jobs",jobs.count(),"applications",applications.count(),"students",students.count(),"openJobs",jobs.findAllByStatusAndApplicationDeadlineGreaterThanEqualOrderByCreatedAtDesc(JobStatus.OPEN,LocalDate.now()).size(),"review",applications.countByStatus(ApplicationStatus.UNDER_REVIEW));return Map.of("jobs",jobs.findAllByStatusAndApplicationDeadlineGreaterThanEqualOrderByCreatedAtDesc(JobStatus.OPEN,LocalDate.now()).size(),"applications",applications.findAllByStudentIdOrderByAppliedAtDesc(user(a).getId()).size(),"profile",profileView(student(a)));}

    private void copyJob(JobPosting j,JobRequest r){j.setTitle(r.title().trim());j.setCompanyName(r.companyName().trim());j.setLocation(r.location().trim());j.setDescription(r.description());j.setEmploymentType(r.employmentType());j.setSalaryRange(r.salaryRange());j.setSource(r.source());j.setStatus(r.status());j.setApplicationDeadline(r.applicationDeadline());}
    private Map<String,Object> jobView(JobPosting j){return Map.ofEntries(Map.entry("id",j.getId()),Map.entry("title",j.getTitle()),Map.entry("companyName",j.getCompanyName()),Map.entry("location",j.getLocation()),Map.entry("description",j.getDescription()),Map.entry("employmentType",Objects.toString(j.getEmploymentType(),"")),Map.entry("salaryRange",Objects.toString(j.getSalaryRange(),"")),Map.entry("source",j.getSource()),Map.entry("status",j.getStatus()),Map.entry("applicationDeadline",j.getApplicationDeadline()),Map.entry("createdAt",j.getCreatedAt()));}
    private Map<String,Object> applicationView(JobApplication x){return Map.ofEntries(Map.entry("id",x.getId()),Map.entry("jobId",x.getJob().getId()),Map.entry("jobTitle",x.getJob().getTitle()),Map.entry("companyName",x.getJob().getCompanyName()),Map.entry("source",x.getJob().getSource()),Map.entry("studentId",x.getStudent().getId()),Map.entry("studentName",x.getStudent().getName()),Map.entry("studentEmail",x.getStudent().getEmail()),Map.entry("department",students.findByUserEmail(x.getStudent().getEmail()).map(Student::getDepartment).orElse("")),Map.entry("status",x.getStatus()),Map.entry("coverLetter",Objects.toString(x.getCoverLetter(),"")),Map.entry("appliedAt",x.getAppliedAt()));}
    private Map<String,Object> profileView(Student s){User u=s.getUser();return Map.of("id",u.getId(),"name",Objects.toString(u.getName(),""),"email",u.getEmail(),"department",Objects.toString(s.getDepartment(),""),"yearOfStudy",Objects.toString(s.getYearOfStudy(),""),"cgpa",Objects.toString(s.getCgpa(),""),"skills",Objects.toString(s.getSkills(),""),"phone",Objects.toString(s.getPhone(),""),"resumeUrl",Objects.toString(s.getResumeUrl(),""));}
    private User user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();} private Student student(Authentication a){return students.findByUserEmail(a.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Student profile not found"));} private boolean isAdmin(Authentication a){return a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));}
}
