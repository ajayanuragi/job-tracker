package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.ApplicationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.ApplicationResponse;
import io.github.ajayanuragi.job_tracker.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "Operations for tracking job applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
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
            description = "Returns all applications belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getUserApplications(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<ApplicationResponse> applications =
                applicationService.getUserApplications(jwt.getSubject());

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
}
