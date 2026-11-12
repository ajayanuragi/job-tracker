package io.github.ajayanuragi.job_tracker.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ApplicationRequest(
        @NotNull Long jobId,
        LocalDateTime appliedAt,
        String notes
) {
}
