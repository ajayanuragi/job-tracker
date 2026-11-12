package io.github.ajayanuragi.job_tracker.specification;

import io.github.ajayanuragi.job_tracker.entity.Application;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.entity.User;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.repository.ApplicationRepository;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import io.github.ajayanuragi.job_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class ApplicationSpecificationsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    void hasStatus() {
        User user = userRepository.save(
                new User("Test User", "test@example.com", "password")
        );

        Job job1 = jobRepository.save(
                new Job(null, "Java Developer", "Company A", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        Job job2 = jobRepository.save(
                new Job(null, "Backend Developer", "Company B", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        applicationRepository.saveAll(List.of(
                new Application(user, job1, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null),
                new Application(user, job2, ApplicationStatus.REJECTED,
                        LocalDateTime.now(), null)
        ));

        List<Application> results = applicationRepository.findAll(
                ApplicationSpecifications.hasStatus(ApplicationStatus.APPLIED)
        );

        assertEquals(1, results.size());
        assertEquals(ApplicationStatus.APPLIED, results.get(0).getStatus());
    }

    @Test
    void hasCompanyName() {
        User user = userRepository.save(
                new User("Test User", "test@example.com", "password")
        );

        Job job1 = jobRepository.save(
                new Job(null, "Java Developer", "Acme Technologies", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        Job job2 = jobRepository.save(
                new Job(null, "Backend Developer", "Tech Solutions", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        applicationRepository.saveAll(List.of(
                new Application(user, job1, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null),
                new Application(user, job2, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null)
        ));

        List<Application> results = applicationRepository.findAll(
                ApplicationSpecifications.hasCompanyName("ACME")
        );

        assertEquals(1, results.size());
        assertEquals("Acme Technologies",
                results.get(0).getJob().getCompanyName());
    }

    @Test
    void hasJobTitle() {
        User user = userRepository.save(
                new User("Test User", "test@example.com", "password")
        );

        Job job1 = jobRepository.save(
                new Job(null, "Senior Java Developer", "Acme Technologies", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        Job job2 = jobRepository.save(
                new Job(null, "Frontend Developer", "Tech Solutions", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        applicationRepository.saveAll(List.of(
                new Application(user, job1, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null),
                new Application(user, job2, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null)
        ));

        List<Application> results = applicationRepository.findAll(ApplicationSpecifications.hasJobTitle("JAVA"));

        assertEquals(1, results.size());
        assertEquals("Senior Java Developer", results.get(0).getJob().getTitle());
    }

    @Test
    void belongsToUser() {
        User user = userRepository.save(
                new User("Test User", "test@example.com", "password")
        );

        User user2 = userRepository.save(new User("Test User2", "test2@example.com", "password"));

        Job job1 = jobRepository.save(
                new Job(null, "Senior Java Developer", "Acme Technologies", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        Job job2 = jobRepository.save(
                new Job(null, "Frontend Developer", "Tech Solutions", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        applicationRepository.save(new Application(user, job1, ApplicationStatus.APPLIED,
                LocalDateTime.now(), null));
        applicationRepository.save(new Application(user2, job2, ApplicationStatus.APPLIED,
                LocalDateTime.now(), null));


        List<Application> results = applicationRepository.findAll(
                ApplicationSpecifications.belongsToUser(user.getId())
        );

        assertEquals(1, results.size());
        assertEquals(user.getId(), results.get(0).getUser().getId());
    }

    @Test
    void filterApplications() {
        User user = userRepository.save(
                new User("Test User", "test@example.com", "password")
        );

        User user2 = userRepository.save(new User("Test User2", "test2@example.com", "password"));

        Job job1 = jobRepository.save(
                new Job(null, "Senior Java Developer", "Acme Technologies", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        Job job2 = jobRepository.save(
                new Job(null, "Backend Developer", "Tech Solutions", null,
                        "Remote", EmploymentType.FULL_TIME,
                        WorkplaceType.REMOTE, null)
        );

        applicationRepository.saveAll(List.of(
                new Application(user, job1, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null),
                new Application(user, job2, ApplicationStatus.REJECTED, LocalDateTime.now(), null),
                new Application(user2, job2, ApplicationStatus.APPLIED,
                        LocalDateTime.now(), null)
        ));

        List<Application> results = applicationRepository.findAll(
                ApplicationSpecifications.filterApplications(user.getId(), ApplicationStatus.APPLIED,"Acme", "Java")
        );


        assertEquals(1, results.size());
        assertEquals("Senior Java Developer", results.get(0).getJob().getTitle());


    }
}