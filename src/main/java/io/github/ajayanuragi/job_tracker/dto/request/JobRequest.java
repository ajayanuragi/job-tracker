package io.github.ajayanuragi.job_tracker.dto.request;

import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JobRequest(
        @NotBlank(message = "Job title is required")
        @Size(max = 150, message = "Job title cannot exceed 150 characters")
        String title,

        @NotBlank(message = "Company name is required")
        @Size(max = 150, message = "Company name cannot exceed 150 characters")
        String companyName,
        @NotBlank(message = "Job description is required")
        String description,
        String location,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @NotNull(message = "Workplace type is required")
        WorkplaceType workplaceType,
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Job URL must start with http:// or https://"
        )
        String jobUrl
) {
}
