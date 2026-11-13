package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.config.SecurityConfig;
import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.service.JobService;
import io.github.ajayanuragi.job_tracker.validation.SortFieldValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(JobController.class)
@Import(SortFieldValidator.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private JobService jobService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllJobs() throws Exception {
        JobResponse job = new JobResponse(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java",
                LocalDateTime.now()
        );

        PageRequest pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<JobResponse> jobPage = new PageImpl<>(List.of(job), pageable, 1);

        when(jobService.findAll(pageable)).thenReturn(jobPage);

        mockMvc.perform(get("/api/jobs")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Java Backend Developer"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));

    }

    @Test
    void getById() throws Exception {
        JobResponse job = new JobResponse(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java",
                LocalDateTime.now()
        );

        when(jobService.findById(1L)).thenReturn(job);
        mockMvc.perform(get("/api/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Backend Developer"));
    }

    @Test
    void create() throws Exception {
        JobRequest request = new JobRequest(
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java"
        );

        JobResponse job = new JobResponse(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java",
                LocalDateTime.now()
        );

        when(jobService.create(request)).thenReturn(job);
        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Backend Developer"));
    }

    @Test
    void deleteJob() throws Exception {
        mockMvc.perform(delete("/api/jobs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateJob() throws Exception {
        JobRequest request = new JobRequest(
                "Senior Java Developer",
                "Acme Technologies",
                "Develop REST APIs and microservices",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/senior-java"
        );
        JobResponse response = new JobResponse(
                1L,
                "Senior Java Developer",
                "Acme Technologies",
                "Develop REST APIs and microservices",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/senior-java",
                LocalDateTime.now()
        );

        when(jobService.update(1L, request)).thenReturn(response);
        mockMvc.perform(put("/api/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Senior Java Developer"));
    }

    @Test
    void patchJob() throws Exception {
        JobPatchRequest request = new JobPatchRequest(
                "Senior Java Developer",
                null,
                null,
                null,
                null,
                null,
                null
        );

        JobResponse response = new JobResponse(
                1L,
                "Senior Java Developer",
                "Acme Technologies",
                "Develop REST APIs",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java",
                LocalDateTime.now()
        );

        when(jobService.patch(1L, request)).thenReturn(response);
        mockMvc.perform(patch("/api/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Senior Java Developer"));
    }

    @Test
    void create_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        String invalidRequest = """
                {
                    "title": "",
                    "companyName": "",
                    "description": "",
                    "employmentType": "FULL_TIME",
                    "workplaceType": "REMOTE"
                }
                """;
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void update_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        String invalidRequest = """
                {
                    "title": "",
                    "companyName": "",
                    "description": "",
                    "employmentType": "FULL_TIME",
                    "workplaceType": "REMOTE"
                }
                """;

        mockMvc.perform(put("/api/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patch_whenTitleExceedsMaxLength_returnsBadRequest() throws Exception {
        String longTitle = "A".repeat(151);

        String invalidRequest = objectMapper.writeValueAsString(
                new JobPatchRequest(longTitle, null, null, null, null, null, null)
        );

        mockMvc.perform(patch("/api/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllJobs_returnsBadRequestForUnsupportedSortField() throws Exception {
        mockMvc.perform(get("/api/jobs")
                        .param("sort", "password,desc")
                        .with(jwt().jwt(jwt -> jwt.subject("test@example.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Sorting by password is not supported"));
    }

    @Test
    void getAllJobs_acceptsSupportedSortField() throws Exception {
        when(jobService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/jobs")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());
    }

}