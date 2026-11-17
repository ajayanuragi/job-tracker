package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.ApplicationPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.ApplicationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.ApplicationResponse;
import io.github.ajayanuragi.job_tracker.entity.Application;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.entity.User;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import io.github.ajayanuragi.job_tracker.exception.ApplicationAlreadyExistsException;
import io.github.ajayanuragi.job_tracker.exception.ResourceNotFoundException;
import io.github.ajayanuragi.job_tracker.repository.ApplicationRepository;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import io.github.ajayanuragi.job_tracker.repository.UserRepository;
import io.github.ajayanuragi.job_tracker.specification.ApplicationSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }


    public ApplicationResponse createApplication(ApplicationRequest applicationRequest, String userEmail) {
        User user = getUserByEmail(userEmail);
        Job job = jobRepository.findById(applicationRequest.jobId()).filter(j -> !j.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), job.getId())) {
            throw new ApplicationAlreadyExistsException(
                    "You have already tracked an application for this job"
            );
        }

        LocalDateTime appliedAt = applicationRequest.appliedAt() != null
                ? applicationRequest.appliedAt()
                : LocalDateTime.now();

        Application application = new Application(
                user,
                job,
                ApplicationStatus.APPLIED,
                appliedAt,
                applicationRequest.notes()
        );

        Application savedApplication = applicationRepository.save(application);

        return mapToResponse(savedApplication);

    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getUserApplications(
            String userEmail,
            ApplicationStatus status,
            String companyName,
            String jobTitle,
            Pageable pageable
            ) {
        User user = getUserByEmail(userEmail);

        Specification<Application> specification =
                ApplicationSpecifications.filterApplications(
                        user.getId(), status, companyName, jobTitle
                );

        return applicationRepository.findAll(specification, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getUserApplication(Long applicationId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return mapToResponse(application);
    }

    @Transactional
    public ApplicationResponse patchApplication(
            Long applicationId,
            ApplicationPatchRequest request,
            String userEmail
    ){
        User user = getUserByEmail(userEmail);

        Application application = applicationRepository.findByIdAndUserId(applicationId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        application.updateDetails(request.status(), request.notes());

        return mapToResponse(application);

    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationByJobId(Long jobId, String userEmail) {
        User user = getUserByEmail(userEmail);

        Application application = applicationRepository
                .findByUserIdAndJobId(user.getId(), jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found")
                );

        return mapToResponse(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        Job job = application.getJob();

        return new ApplicationResponse(
                application.getId(),
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                application.getStatus(),
                application.getAppliedAt(),
                application.getNotes(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
