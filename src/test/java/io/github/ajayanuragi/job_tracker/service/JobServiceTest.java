package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.exception.ResourceNotFoundException;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobRequest request = new JobRequest(
                "Senior Java Backend Developer",
                "Tech Solutions",
                "Build scalable REST APIs using Spring Boot.",
                "Bangalore",
                EmploymentType.FULL_TIME,
                WorkplaceType.HYBRID,
                "https://example.com/jobs/senior-java"
        );

        JobResponse response = jobService.update(1L, request);
        assertEquals(1L, response.id());
        assertEquals("Senior Java Backend Developer", response.title());
        assertEquals("Tech Solutions", response.companyName());
        assertEquals("Build scalable REST APIs using Spring Boot.", response.description());
        assertEquals("Bangalore", response.location());
        assertEquals(EmploymentType.FULL_TIME, response.employmentType());
        assertEquals(WorkplaceType.HYBRID, response.workplaceType());
        assertEquals("https://example.com/jobs/senior-java", response.jobUrl());

        verify(jobRepository).findById(1L);
        verify(jobRepository).save(job);

    }

    @Test
    void patch() {
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
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        JobPatchRequest request = new JobPatchRequest(
                "Senior Java Backend Developer",
                null,
                null,
                "Bangalore",
                null,
                null,
                null
        );
        JobResponse response = jobService.patch(1L, request);
        assertEquals(1L, response.id());
        assertEquals("Senior Java Backend Developer", response.title());
        assertEquals("Acme Technologies", response.companyName());
        assertEquals("Bangalore", response.location());
        assertEquals("Develop REST APIs using Spring Boot.", response.description());
        assertEquals(EmploymentType.FULL_TIME, response.employmentType());
        assertEquals(WorkplaceType.REMOTE, response.workplaceType());
        assertEquals("https://example.com/jobs/java", response.jobUrl());
        verify(jobRepository).findById(1L);
        verify(jobRepository).save(job);
    }

    @Test
    void findById_whenJobDoesNotExist_throwsResourceNotFoundException() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> jobService.findById(1L));

        verify(jobRepository).findById(1L);

    }

    @Test
    void findById_whenJobIsDeleted_throwsResourceNotFoundException() {
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

        job.softDelete();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.findById(1L)
        );

        verify(jobRepository).findById(1L);
    }


    @Test
    void findAllPaginated() {
        Job job1 = new Job(
                1L, "Java Backend Developer", "Acme Technologies",
                "Develop REST APIs.", "Remote",
                EmploymentType.FULL_TIME, WorkplaceType.REMOTE,
                "https://example.com/jobs/java"
        );

        Job job2 = new Job(
                2L, "React Developer", "Tech Solutions",
                "Build React applications.", "Bangalore",
                EmploymentType.CONTRACT, WorkplaceType.HYBRID,
                "https://example.com/jobs/react"
        );

        PageRequest pageable = PageRequest.of(0, 2);

        Page<Job> jobPage = new PageImpl<>(List.of(job1, job2), pageable, 2);

        when(jobRepository.findAllByDeletedFalse(pageable))
                .thenReturn(jobPage);

        Page<JobResponse> response = jobService.findAll(pageable);

        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getTotalElements());
        assertEquals(1L, response.getContent().getFirst().id());
        assertEquals("Java Backend Developer", response.getContent().getFirst().title());

        verify(jobRepository).findAllByDeletedFalse(pageable);
    }
}