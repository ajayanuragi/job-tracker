package io.github.ajayanuragi.job_tracker.dto.request;

import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JobPatchRequest(

        @Size(max = 150, message = "Job title cannot exceed 150 characters")
        String title,


        @Size(max = 150, message = "Company name cannot exceed 150 characters")
        String companyName,


        String description,


        String location,

        EmploymentType employmentType,

        WorkplaceType workplaceType,

        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Job URL must start with http:// or https://"
        )
        String jobUrl
) {
}
