package io.github.ajayanuragi.job_tracker.entity;

import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String companyName;
    private String description;
    private String location;
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    @Enumerated(EnumType.STRING)
    private WorkplaceType workplaceType;
    private String jobUrl;
    @Column(nullable = false)
    private Boolean deleted = false;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Job(Long id, String title, String companyName, String description, String location, EmploymentType employmentType, WorkplaceType workplaceType, String jobUrl) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.location = location;
        this.employmentType = employmentType;
        this.workplaceType = workplaceType;
        this.jobUrl = jobUrl;
        this.deleted = false;
    }

    public Job() {

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public WorkplaceType getWorkplaceType() {
        return workplaceType;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateDetails(
            String title,
            String companyName,
            String description,
            String location,
            EmploymentType employmentType,
            WorkplaceType workplaceType,
            String jobUrl
    ) {
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.location = location;
        this.employmentType = employmentType;
        this.workplaceType = workplaceType;
        this.jobUrl = jobUrl;
    }


    public void patchDetails(
            String title,
            String companyName,
            String description,
            String location,
            EmploymentType employmentType,
            WorkplaceType workplaceType,
            String jobUrl
    ) {
        if (title != null) this.title = title;
        if (companyName != null) this.companyName = companyName;
        if (description != null) this.description = description;
        if (location != null) this.location = location;
        if (employmentType != null) this.employmentType = employmentType;
        if (workplaceType != null) this.workplaceType = workplaceType;
        if (jobUrl != null) this.jobUrl = jobUrl;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
