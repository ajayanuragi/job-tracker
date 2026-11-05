package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;


    @Test
    void create() {
        JobRequest request = new JobRequest(
                "Java Backend Developer",
                "Acme Technologies",
                "Looking for a Java developer with Spring Boot experience.",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java-developer"
        );
        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> {
                    Job job = invocation.getArgument(0);

                    return new Job(
                            1L,
                            job.getTitle(),
                            job.getCompanyName(),
                            job.getDescription(),
                            job.getLocation(),
                            job.getEmploymentType(),
                            job.getWorkplaceType(),
                            job.getJobUrl()
                    );
                });
        JobResponse response = jobService.create(request);
        assertEquals(1L, response.id());
        assertEquals(request.title(), response.title());
        assertEquals(request.companyName(), response.companyName());
        assertEquals(request.description(), response.description());
        assertEquals(request.location(), response.location());
        assertEquals(request.employmentType(), response.employmentType());
        assertEquals(request.workplaceType(), response.workplaceType());
        assertEquals(request.jobUrl(), response.jobUrl());

        verify(jobRepository).save(any(Job.class));

    }

    @Test
    void findAll() {
        Job job1 = new Job(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs using Java and Spring Boot.",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java-backend"
        );

        Job job2 = new Job(
                2L,
                "React Developer",
                "Tech Solutions",
                "Build responsive web applications using React and TypeScript.",
                "Bangalore",
                EmploymentType.CONTRACT,
                WorkplaceType.HYBRID,
                "https://example.com/jobs/react-developer"
        );

        List<Job> jobs =  List.of(job1, job2);
        when(jobRepository.findByDeletedFalse()).thenReturn(jobs);
        List<JobResponse> jobResponses = jobService.findAll();

        assertEquals(2, jobResponses.size());
        assertEquals(1L, jobResponses.getFirst().id());
        assertEquals("Java Backend Developer", jobResponses.getFirst().title());
        assertEquals("Acme Technologies", jobResponses.getFirst().companyName());

        verify(jobRepository).findByDeletedFalse();

    }

    @Test
    void findById() {
        Job job = new Job(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs using Spring Boot.",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java"
        );
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        JobResponse response = jobService.findById(1L);
        assertEquals(1L, response.id());
        assertEquals("Java Backend Developer", response.title());
        assertEquals("Acme Technologies", response.companyName());

        verify(jobRepository).findById(1L);

    }

    @Test
    void deleteById() {
        Job job = new Job(
                1L,
                "Java Backend Developer",
                "Acme Technologies",
                "Develop REST APIs using Spring Boot.",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/jobs/java"
        );
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        jobService.deleteById(1L);
        assertTrue(job.isDeleted());
        verify(jobRepository).findById(1L);
        verify(jobRepository).save(job);

    }

    @Test
    void update() {
    }

    @Test
    void patch() {
    }
}