package io.github.ajayanuragi.job_tracker.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        int Status,
        String error,
        String message,
        LocalDateTime timestamp

) {
}