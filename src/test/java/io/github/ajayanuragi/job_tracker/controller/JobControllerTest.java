package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(JobController.class)
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
                "https://example.com/jobs/java"
        );

        when(jobService.findAll()).thenReturn(List.of(job));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Java Backend Developer"));
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
                "https://example.com/jobs/java"
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
                "https://example.com/jobs/java"
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
                "https://example.com/jobs/senior-java"
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
                "https://example.com/jobs/java"
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


}