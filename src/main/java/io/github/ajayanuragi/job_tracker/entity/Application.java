package io.github.ajayanuragi.job_tracker.entity;

import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_job_application",
                        columnNames = {"user_id", "job_id"})
        }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @Column(nullable = false)
    private LocalDateTime appliedAt;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Application(User user, Job job, ApplicationStatus status,
                       LocalDateTime appliedAt, String notes) {
        this.user = user;
        this.job = job;
        this.status = status;
        this.appliedAt = appliedAt;
        this.notes = notes;
    }

    protected Application() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateDetails(ApplicationStatus status, String notes) {
        if (status != null) {
            this.status = status;
        }

        if (notes != null) {
            this.notes = notes;
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Job getJob() {
        return job;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
