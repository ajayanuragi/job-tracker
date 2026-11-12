package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.config.SecurityConfig;
import io.github.ajayanuragi.job_tracker.dto.request.ApplicationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.ApplicationResponse;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import io.github.ajayanuragi.job_tracker.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@Import(SecurityConfig.class)
class ApplicationControllerTest {
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService;


    @Test
    void createApplication() throws Exception {
        LocalDateTime appliedAt = LocalDateTime.now();

        ApplicationResponse response = new ApplicationResponse(
                1L,
                2L,
                "Java Developer",
                "ABC Company",
                ApplicationStatus.APPLIED,
                appliedAt,
                "Applied through company website",
                appliedAt,
                appliedAt
        );

        when(applicationService.createApplication(
                any(ApplicationRequest.class),
                eq("test@example.com")
        )).thenReturn(response);

        mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(jwt -> jwt.subject("test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 2,
                                    "appliedAt": "%s",
                                    "notes": "Applied through company website"
                                }
                                """.formatted(appliedAt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.jobId").value(2))
                .andExpect(jsonPath("$.jobTitle").value("Java Developer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void getUserApplications() throws Exception {
        LocalDateTime appliedAt = LocalDateTime.now();

        ApplicationResponse response = new ApplicationResponse(
                1L, 2L, "Java Developer", "ABC Company",
                ApplicationStatus.APPLIED, appliedAt,
                "Applied through company website", appliedAt, appliedAt
        );

        when(applicationService.getUserApplications(
                eq("test@example.com"),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(response)));


        mockMvc.perform(get("/api/applications")
                        .with(jwt().jwt(jwt -> jwt.subject("test@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].jobId").value(2))
                .andExpect(jsonPath("$.content[0].jobTitle").value("Java Developer"))
                .andExpect(jsonPath("$.content[0].status").value("APPLIED"));
    }

    @Test
    void getUserApplication() throws Exception {
        LocalDateTime appliedAt = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(
                1L, 2L, "Java Developer", "ABC Company",
                ApplicationStatus.APPLIED, appliedAt,
                "Applied through company website", appliedAt, appliedAt
        );
        when(applicationService.getUserApplication(1L, "test@example.com"))
                .thenReturn(response);

        mockMvc.perform(get("/api/applications/1")
                        .with(jwt().jwt(jwt -> jwt.subject("test@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.jobId").value(2))
                .andExpect(jsonPath("$.jobTitle").value("Java Developer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));


    }

    @Test
    void createApplication_returnsBadRequestWhenJobIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .with(jwt().jwt(jwt -> jwt.subject("test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "appliedAt": "2026-11-12T10:00:00",
                                    "notes": "Applied through company website"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserApplications_returnsUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isUnauthorized());
    }
}