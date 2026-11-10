package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.LoginRequest;
import io.github.ajayanuragi.job_tracker.dto.request.UserRegistrationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.LoginResponse;
import io.github.ajayanuragi.job_tracker.exception.EmailAlreadyExistsException;
import io.github.ajayanuragi.job_tracker.exception.InvalidCredentialsException;
import io.github.ajayanuragi.job_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.github.ajayanuragi.job_tracker.dto.request.UserRegistrationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.UserResponse;
import io.github.ajayanuragi.job_tracker.entity.User;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Ajay",
                "ajay@example.com",
                "password123"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.registerUser(request);

        assertEquals("Ajay", response.name());
        assertEquals("ajay@example.com", response.email());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
        verify(passwordEncoder).encode("password123");

    }

    @Test
    void registerUserWithExistingEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Ajay",
                "ajay@example.com",
                "password123"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login() {
        LoginRequest request = new LoginRequest(
                "ajay@example.com",
                "password123"
        );

        User user = new User("Ajay", "ajay@example.com", "encodedPassword");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail())).thenReturn("mock-jwt-token");

        LoginResponse response = userService.login(request);

        assertEquals("mock-jwt-token", response.token());

        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken("ajay@example.com");
    }

    @Test
    void loginWithIncorrectPassword() {
        LoginRequest request = new LoginRequest(
                "ajay@example.com",
                "wrongpassword"
        );

        User user = new User("Ajay", "ajay@example.com", "encodedPassword");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginWithUnknownEmail() {
        LoginRequest request = new LoginRequest(
                "unknown@example.com",
                "password123"
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }
}