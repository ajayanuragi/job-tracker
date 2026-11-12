package io.github.ajayanuragi.job_tracker.specification;

import io.github.ajayanuragi.job_tracker.entity.Application;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ApplicationSpecifications {

    public static Specification<Application> hasStatus(ApplicationStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Application> hasCompanyName(String companyName) {
        return ((root, query, criteriaBuilder) -> {
            if (companyName == null || companyName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<Application, Job> jobJoin = root.join("job");
            return criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("companyName")), "%" + companyName.toLowerCase() + "%");
        });
    }

    public static Specification<Application> hasJobTitle(String jobTitle) {
        return ((root, query, criteriaBuilder) -> {
            if (jobTitle == null || jobTitle.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<Application, Job> jobJoin = root.join("job");

            return criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("title")), "%" + jobTitle.toLowerCase() + "%");
        });
    }

    public static Specification<Application> belongsToUser(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }


    public static Specification<Application> filterApplications(
            Long userId,
            ApplicationStatus status,
            String companyName,
            String jobTitle
    ) {
        return Specification.where(belongsToUser(userId))
                .and(hasStatus(status))
                .and(hasCompanyName(companyName))
                .and(hasJobTitle(jobTitle));
    }

}
