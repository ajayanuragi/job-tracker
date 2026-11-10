package io.github.ajayanuragi.job_tracker.service;

import io.github.ajayanuragi.job_tracker.dto.request.UserRegistrationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.UserResponse;
import io.github.ajayanuragi.job_tracker.entity.User;
import io.github.ajayanuragi.job_tracker.exception.EmailAlreadyExistsException;
import io.github.ajayanuragi.job_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.name(),
                request.email(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);


    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );

    }

}
