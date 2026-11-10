package io.github.ajayanuragi.job_tracker.dto.response;

import java.time.LocalDateTime;

public record UserResponse(Long id,
                           String name, String email, LocalDateTime createdAt) {
}
