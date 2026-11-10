package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(Pageable pageable) {
         Page<JobResponse> jobs = jobService.findAll(pageable);
        return ResponseEntity.ok(jobs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> findById(@PathVariable Long id) {
        return new ResponseEntity<>(jobService.findById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest job) {
        JobResponse newJob = jobService.create(job);
        return new ResponseEntity<>(newJob, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest job) {
        JobResponse updatedJob = jobService.update(id, job);
        return ResponseEntity.ok(updatedJob);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<JobResponse> patchJob(@PathVariable Long id, @Valid @RequestBody JobPatchRequest job) {
        JobResponse updatedJob = jobService.patch(id, job);
        return ResponseEntity.ok(updatedJob);
    }


}
