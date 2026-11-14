CREATE INDEX idx_applications_user_applied_at
    ON applications (user_id, applied_at DESC);

CREATE INDEX idx_applications_user_status_applied_at
    ON applications (user_id, status, applied_at DESC);

CREATE INDEX idx_job_active_created_at
    ON job (created_at DESC)
    WHERE deleted = false;