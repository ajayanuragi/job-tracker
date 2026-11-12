package io.github.ajayanuragi.job_tracker.dto.response;

import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String companyName,
        ApplicationStatus status,
        LocalDateTime appliedAt,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
