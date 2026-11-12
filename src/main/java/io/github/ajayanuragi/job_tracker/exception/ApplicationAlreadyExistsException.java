package io.github.ajayanuragi.job_tracker.exception;

public class ApplicationAlreadyExistsException extends RuntimeException {
    public ApplicationAlreadyExistsException(String message) {
        super(message);
    }
}
