package io.github.ajayanuragi.job_tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank
        @Email @Size(max = 100) String email,
        @Schema(example = "SecurePass123!")
        @NotBlank
        @Size(min = 8, max = 72)
        String password) {
}
