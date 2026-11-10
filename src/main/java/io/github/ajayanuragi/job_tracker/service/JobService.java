package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.JobPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.JobRequest;
import io.github.ajayanuragi.job_tracker.dto.response.JobResponse;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.exception.ResourceNotFoundException;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponse create(JobRequest request) {
        Job job = new Job(
                null,
                request.title(),
                request.companyName(),
                request.description(),
                request.location(),
                request.employmentType(),
                request.workplaceType(),
                request.jobUrl()
        );
        Job savedJob = jobRepository.save(job);
        return new JobResponse(
                savedJob.getId(),
                savedJob.getTitle(),
                savedJob.getCompanyName(),
                savedJob.getDescription(),
                savedJob.getLocation(),
                savedJob.getEmploymentType(),
                savedJob.getWorkplaceType(),
                savedJob.getJobUrl(),
                savedJob.getCreatedAt()
        );
    }

    public JobResponse findById(Long id) {
        return mapToResponse(getActiveJob(id));
    }

    private JobResponse mapToResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getWorkplaceType(),
                job.getJobUrl(),
                job.getCreatedAt()
        );
    }

    public void deleteById(Long id) {
        Job job = getActiveJob(id);
        job.softDelete();
        jobRepository.save(job);
    }

    public JobResponse update(Long id, JobRequest request) {
        Job job = getActiveJob(id);
        job.updateDetails(
                request.title(),
                request.companyName(),
                request.description(),
                request.location(),
                request.employmentType(),
                request.workplaceType(),
                request.jobUrl()
        );
        Job updatedJob = jobRepository.save(job);
        return mapToResponse(updatedJob);
    }

    public JobResponse patch(Long id, JobPatchRequest request) {
        Job job = getActiveJob(id);
        job.patchDetails(
                request.title(),
                request.companyName(),
                request.description(),
                request.location(),
                request.employmentType(),
                request.workplaceType(),
                request.jobUrl()
        );
        Job updatedJob = jobRepository.save(job);
        return mapToResponse(updatedJob);

    }


    private Job getActiveJob(Long id) {
        return jobRepository.findById(id)
                .filter(job -> !job.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found with ID: " + id));
    }

    public Page<JobResponse> findAll(Pageable pageable) {
        return jobRepository.findAllByDeletedFalse(pageable).map(this::mapToResponse);
    }
}
