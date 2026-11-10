package io.github.ajayanuragi.job_tracker.dto.response;

import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;

import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String title,
        String companyName,
        String description,
        String location,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        String jobUrl,
        LocalDateTime createdAt
) {
}
