package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.ApplicationPatchRequest;
import io.github.ajayanuragi.job_tracker.dto.request.ApplicationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.ApplicationResponse;
import io.github.ajayanuragi.job_tracker.entity.Application;
import io.github.ajayanuragi.job_tracker.entity.Job;
import io.github.ajayanuragi.job_tracker.entity.User;
import io.github.ajayanuragi.job_tracker.enums.ApplicationStatus;
import io.github.ajayanuragi.job_tracker.enums.EmploymentType;
import io.github.ajayanuragi.job_tracker.enums.WorkplaceType;
import io.github.ajayanuragi.job_tracker.exception.ApplicationAlreadyExistsException;
import io.github.ajayanuragi.job_tracker.exception.ResourceNotFoundException;
import io.github.ajayanuragi.job_tracker.repository.ApplicationRepository;
import io.github.ajayanuragi.job_tracker.repository.JobRepository;
import io.github.ajayanuragi.job_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JobRepository jobRepository;


    @Test
    void createApplication() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = new Job(
                2L,
                "Java Developer",
                "ABC Company",
                "Java backend role",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/job"
        );

        when(jobRepository.findById(2L)).thenReturn(Optional.of(job));

        when(applicationRepository.existsByUserIdAndJobId(1L, 2L))
                .thenReturn(false);

        ApplicationRequest request = new ApplicationRequest(
                2L,
                LocalDateTime.now(),
                "Applied through company website"
        );

        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse response =
                applicationService.createApplication(request, email);

        assertEquals(2L, response.jobId());
        assertEquals("Java Developer", response.jobTitle());
        assertEquals("ABC Company", response.companyName());
        assertEquals(ApplicationStatus.APPLIED, response.status());
    }

    @Test
    void createApplication_throwsExceptionWhenAlreadyExists() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = new Job(
                2L,
                "Java Developer",
                "ABC Company",
                "Java backend role",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/job"
        );

        when(jobRepository.findById(2L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByUserIdAndJobId(1L, 2L))
                .thenReturn(true);

        ApplicationRequest request = new ApplicationRequest(
                2L, LocalDateTime.now(), null
        );

        assertThrows(
                ApplicationAlreadyExistsException.class,
                () -> applicationService.createApplication(request, email)
        );

        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void createApplication_throwsExceptionWhenJobNotFound() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jobRepository.findById(2L)).thenReturn(Optional.empty());

        ApplicationRequest request = new ApplicationRequest(
                2L,
                LocalDateTime.now(),
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.createApplication(request, email)
        );

        verify(applicationRepository, never()).save(any(Application.class));

    }

    @Test
    void createApplication_throwsExceptionWhenUserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        ApplicationRequest request = new ApplicationRequest(
                2L,
                LocalDateTime.now(),
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.createApplication(request, email)
        );

        verify(jobRepository, never()).findById(anyLong());
        verify(applicationRepository, never()).save(any(Application.class));

    }

    @Test
    void createApplication_usesCurrentTimeWhenAppliedAtIsNull() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = new Job(
                2L,
                "Java Developer",
                "ABC Company",
                "Java backend role",
                "Remote",
                EmploymentType.FULL_TIME,
                WorkplaceType.REMOTE,
                "https://example.com/job"
        );

        when(jobRepository.findById(2L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByUserIdAndJobId(1L, 2L))
                .thenReturn(false);
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime before = LocalDateTime.now();

        ApplicationRequest request = new ApplicationRequest(2L, null, null);

        ApplicationResponse response =
                applicationService.createApplication(request, email);

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response.appliedAt());
        assertFalse(response.appliedAt().isBefore(before));
        assertFalse(response.appliedAt().isAfter(after));
    }

    @Test
    void getUserApplications_returnsUserApplications() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = mock(Job.class);
        when(job.getId()).thenReturn(2L);
        when(job.getTitle()).thenReturn("Java Developer");
        when(job.getCompanyName()).thenReturn("ABC Company");

        Application application = mock(Application.class);
        when(application.getJob()).thenReturn(job);
        when(application.getId()).thenReturn(3L);
        when(application.getStatus()).thenReturn(ApplicationStatus.APPLIED);

        when(applicationRepository.findAll(
                ArgumentMatchers.<Specification<Application>>any(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(application)));

        Page<ApplicationResponse> responses =
                applicationService.getUserApplications(
                        email,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, responses.getTotalElements());
        assertEquals(2L, responses.getContent().getFirst().jobId());
        assertEquals("Java Developer", responses.getContent().getFirst().jobTitle());
        assertEquals("ABC Company", responses.getContent().getFirst().companyName());
        assertEquals(ApplicationStatus.APPLIED, responses.getContent().getFirst().status());

        verify(applicationRepository).findAll(
                ArgumentMatchers.<Specification<Application>>any(),
                any(Pageable.class)
        );
    }

    @Test
    void getUserApplication_returnsApplication() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = mock(Job.class);
        when(job.getId()).thenReturn(2L);
        when(job.getTitle()).thenReturn("Java Developer");
        when(job.getCompanyName()).thenReturn("ABC Company");

        Application application = mock(Application.class);
        when(application.getJob()).thenReturn(job);
        when(application.getId()).thenReturn(3L);
        when(application.getStatus()).thenReturn(ApplicationStatus.INTERVIEW);

        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.of(application));

        ApplicationResponse response =
                applicationService.getUserApplication(3L, email);

        assertEquals(3L, response.id());
        assertEquals(2L, response.jobId());
        assertEquals("Java Developer", response.jobTitle());
        assertEquals(ApplicationStatus.INTERVIEW, response.status());

        verify(applicationRepository).findByIdAndUserId(3L, 1L);
    }

    @Test
    void getUserApplication_throwsExceptionWhenApplicationNotFound() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getUserApplication(3L, email)
        );

        verify(applicationRepository).findByIdAndUserId(3L, 1L);
    }


    @Test
    void getUserApplications_throwsExceptionWhenUserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getUserApplications(
                        email,
                        null,
                        null,
                        null,
                        Pageable.unpaged())
        );

        verify(applicationRepository, never())
                .findAll(
                        ArgumentMatchers.<Specification<Application>>any(),
                        any(Pageable.class)
                );
    }

    @Test
    void getUserApplication_throwsExceptionWhenUserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getUserApplication(3L, email)
        );

        verify(applicationRepository, never())
                .findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void patchApplication_updatesStatusAndNotes() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = mock(Job.class);
        when(job.getId()).thenReturn(2L);
        when(job.getTitle()).thenReturn("Java Developer");
        when(job.getCompanyName()).thenReturn("ABC Company");

        Application application = mock(Application.class);
        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.of(application));
        when(application.getJob()).thenReturn(job);
        when(application.getId()).thenReturn(3L);
        when(application.getStatus()).thenReturn(ApplicationStatus.INTERVIEW);
        when(application.getNotes()).thenReturn("Technical interview scheduled");

        ApplicationPatchRequest request = new ApplicationPatchRequest(
                ApplicationStatus.INTERVIEW,
                "Technical interview scheduled"
        );

        ApplicationResponse response =
                applicationService.patchApplication(3L, request, email);

        verify(application).updateDetails(
                ApplicationStatus.INTERVIEW,
                "Technical interview scheduled"
        );

        assertEquals(ApplicationStatus.INTERVIEW, response.status());
        assertEquals("Technical interview scheduled", response.notes());
    }

    @Test
    void patchApplication_clearsNotesWhenEmptyStringProvided() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Application application = mock(Application.class);

        Job job = mock(Job.class);

        when(job.getId()).thenReturn(2L);
        when(job.getTitle()).thenReturn("Java Developer");
        when(job.getCompanyName()).thenReturn("ABC Company");

        when(application.getJob()).thenReturn(job);
        when(application.getId()).thenReturn(3L);
        when(application.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(application.getNotes()).thenReturn("");

        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.of(application));

        ApplicationPatchRequest request =
                new ApplicationPatchRequest(null, "");

        ApplicationResponse response =
                applicationService.patchApplication(3L, request, email);

        verify(application).updateDetails(null, "");

        assertEquals("", response.notes());
    }

    @Test
    void patchApplication_keepsStatusWhenStatusIsNull() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Application application = mock(Application.class);
        Job job = mock(Job.class);

        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.of(application));

        when(application.getJob()).thenReturn(job);
        when(application.getId()).thenReturn(3L);
        when(application.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(application.getNotes()).thenReturn("Updated notes");

        ApplicationPatchRequest request =
                new ApplicationPatchRequest(null, "Updated notes");

        ApplicationResponse response =
                applicationService.patchApplication(3L, request, email);

        verify(application).updateDetails(null, "Updated notes");

        assertEquals(ApplicationStatus.APPLIED, response.status());
        assertEquals("Updated notes", response.notes());
    }

    @Test
    void patchApplication_throwsExceptionWhenApplicationNotFound() {
        String email = "test@example.com";

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        when(applicationRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.empty());

        ApplicationPatchRequest request =
                new ApplicationPatchRequest(ApplicationStatus.INTERVIEW, "Updated notes");

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.patchApplication(3L, request, email)
        );

        verify(applicationRepository).findByIdAndUserId(3L, 1L);
    }

    @Test
    void applicationExists_returnsTrueWhenTracked() {
        String email = "test@example.com";
        Long jobId = 2L;

        User user = mock(User.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        when(applicationRepository.existsByUserIdAndJobId(1L, jobId))
                .thenReturn(true);

        boolean exists = applicationService.applicationExists(jobId, email);

        assertTrue(exists);

        verify(applicationRepository)
                .existsByUserIdAndJobId(1L, jobId);
    }


    @Test
    void applicationExists_returnsFalseWhenNotTracked() {
        String email = "test@example.com";
        Long jobId = 2L;

        User user = mock(User.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        when(applicationRepository.existsByUserIdAndJobId(1L, jobId))
                .thenReturn(false);

        boolean exists = applicationService.applicationExists(jobId, email);

        assertFalse(exists);

        verify(applicationRepository)
                .existsByUserIdAndJobId(1L, jobId);
    }

    @Test
    void applicationExists_throwsExceptionWhenUserNotFound() {
        String email = "unknown@example.com";
        Long jobId = 2L;

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.applicationExists(jobId, email)
        );

        verify(applicationRepository, never())
                .existsByUserIdAndJobId(anyLong(), anyLong());
    }

    @Test
    void getApplicationByJobId_returnsApplication() {
        String email = "test@example.com";
        Long jobId = 2L;

        User user = mock(User.class);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        Job job = mock(Job.class);
        when(job.getId()).thenReturn(jobId);
        when(job.getTitle()).thenReturn("Java Developer");
        when(job.getCompanyName()).thenReturn("ABC Company");

        Application application = mock(Application.class);
        when(application.getId()).thenReturn(3L);
        when(application.getJob()).thenReturn(job);
        when(application.getStatus()).thenReturn(ApplicationStatus.APPLIED);

        when(applicationRepository.findByUserIdAndJobId(1L, jobId))
                .thenReturn(Optional.of(application));

        ApplicationResponse response =
                applicationService.getApplicationByJobId(jobId, email);

        assertEquals(3L, response.id());
        assertEquals(2L, response.jobId());
        assertEquals("Java Developer", response.jobTitle());
        assertEquals(ApplicationStatus.APPLIED, response.status());

        verify(applicationRepository).findByUserIdAndJobId(1L, jobId);
    }

    @Test
    void getApplicationByJobId_throwsExceptionWhenApplicationNotFound() {
        String email = "test@example.com";
        Long jobId = 2L;

        User user = mock(User.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);

        when(applicationRepository.findByUserIdAndJobId(1L, jobId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getApplicationByJobId(jobId, email)
        );

        verify(applicationRepository)
                .findByUserIdAndJobId(1L, jobId);
    }

    @Test
    void getApplicationByJobId_throwsExceptionWhenUserNotFound() {
        String email = "unknown@example.com";
        Long jobId = 2L;

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> applicationService.getApplicationByJobId(jobId, email)
        );

        verify(applicationRepository, never())
                .findByUserIdAndJobId(anyLong(), anyLong());
    }
}