package io.github.ajayanuragi.job_tracker.dto.request;

import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;

public record ApplicationPatchRequest(
        ApplicationStatus status,
        String notes
) {}