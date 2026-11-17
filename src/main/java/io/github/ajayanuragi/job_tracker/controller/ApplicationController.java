package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.ApplicationPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.ApplicationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.ApplicationResponse;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import io.github.ajayanuragi.job_tracker.service.ApplicationService;
import io.github.ajayanuragi.job_tracker.validation.SortFieldValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Operations for tracking job applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final SortFieldValidator sortFieldValidator;

    public ApplicationController(ApplicationService applicationService, SortFieldValidator sortFieldValidator) {
        this.applicationService = applicationService;
        this.sortFieldValidator = sortFieldValidator;
    }

    @Operation(summary = "Track a job application",
            description = "Creates an application record for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "User or job not found"),
            @ApiResponse(responseCode = "409", description = "Application already exists for this job")
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest applicationRequest, @AuthenticationPrincipal Jwt jwt) {
        ApplicationResponse response = applicationService.createApplication(applicationRequest, jwt.getSubject());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all tracked applications",
            description = "Returns the authenticated user's applications with optional filtering, pagination, and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> getUserApplications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String jobTitle,
            @ParameterObject Pageable pageable
    ) {
        sortFieldValidator.validate(
                pageable,
                Set.of("appliedAt", "createdAt", "updatedAt", "status")
        );


        Page<ApplicationResponse> applications =
                applicationService.getUserApplications(jwt.getSubject(), status, companyName, jobTitle, pageable);

        return ResponseEntity.ok(applications);
    }


    @Operation(summary = "Get an application by ID",
            description = "Returns an application belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getUserApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApplicationResponse application =
                applicationService.getUserApplication(id, jwt.getSubject());

        return ResponseEntity.ok(application);
    }

    @Operation(
            summary = "Update an application",
            description = "Partially updates the status and notes of an application belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(@PathVariable Long id, @RequestBody ApplicationPatchRequest request, @AuthenticationPrincipal Jwt jwt) {

        ApplicationResponse response = applicationService.patchApplication(id, request, jwt.getSubject());
        return ResponseEntity.ok(response);

    }


    @Operation(
            summary = "Check whether an application exists",
            description = "Checks whether the authenticated user has already tracked a specific job"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid job ID"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/exists")
    public ResponseEntity<Boolean> applicationExists(
            @RequestParam Long jobId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        boolean exists = applicationService.applicationExists(
                jobId,
                jwt.getSubject()
        );

        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Get an application by job ID",
            description = "Returns the authenticated user's application for a specific job"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @GetMapping("/by-job")
    public ResponseEntity<ApplicationResponse> getApplicationByJobId(
            @RequestParam Long jobId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApplicationResponse response =
                applicationService.getApplicationByJobId(jobId, jwt.getSubject());

        return ResponseEntity.ok(response);
    }
}
