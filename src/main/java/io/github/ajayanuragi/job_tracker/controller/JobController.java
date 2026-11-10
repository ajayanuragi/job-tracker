package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Jobs", description = "Operations for managing job listings")
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
            summary = "Get all jobs",
            description = "Returns a paginated list of active jobs with sorting support"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters")
    })
    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @Parameter(
                    description =
                            "Pagination and sorting. Supported sort fields: createdAt, title, companyName"
            ) @ParameterObject Pageable pageable) {
        Page<JobResponse> jobs = jobService.findAll(pageable);
        return ResponseEntity.ok(jobs);
    }

    @Operation(
            summary = "Get job by ID",
            description = "Returns an active job by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> findById(@PathVariable Long id) {
        return new ResponseEntity<>(jobService.findById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a job",
            description = "Creates a new job listing"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Job created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping()
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest job) {
        JobResponse newJob = jobService.create(job);
        return new ResponseEntity<>(newJob, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Delete a job",
            description = "Soft-deletes a job listing"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Job deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a job",
            description = "Replaces all editable fields of an existing job"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest job) {
        JobResponse updatedJob = jobService.update(id, job);
        return ResponseEntity.ok(updatedJob);
    }

    @Operation(
            summary = "Partially update a job",
            description = "Updates only the provided fields of an existing job"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job partially updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<JobResponse> patchJob(@PathVariable Long id, @Valid @RequestBody JobPatchRequest job) {
        JobResponse updatedJob = jobService.patch(id, job);
        return ResponseEntity.ok(updatedJob);
    }


}
