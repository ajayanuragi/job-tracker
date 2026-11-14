package io.github.ajayanuragi.job_tracker.repository;

import io.github.ajayanuragi.job_tracker.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAllByDeletedFalse(Pageable pageable);

}
