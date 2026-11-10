package io.github.ajayanuragi.job_tracker.controller;

import io.github.ajayanuragi.job_tracker.dto.request.LoginRequest;
import io.github.ajayanuragi.job_tracker.dto.request.UserRegistrationRequest;
import io.github.ajayanuragi.job_tracker.dto.response.LoginResponse;
import io.github.ajayanuragi.job_tracker.dto.response.UserResponse;
import io.github.ajayanuragi.job_tracker.exception.InvalidCredentialsException;
import io.github.ajayanuragi.job_tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Ajay",
                "ajay@example.com",
                "password123"
        );

        UserResponse response = new UserResponse(
                1L,
                "Ajay",
                "ajay@example.com",
                LocalDateTime.now()
        );

        when(userService.registerUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ajay"))
                .andExpect(jsonPath("$.email").value("ajay@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void login() throws Exception {
        LoginRequest request = new LoginRequest(
                "ajay@example.com",
                "password123"
        );

        LoginResponse response = new LoginResponse("mock-jwt-token");

        when(userService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }


    @Test
    void registerUser_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        String invalidRequest = """
            {
                "name": "",
                "email": "invalid-email",
                "password": ""
            }
            """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        String invalidRequest = """
            {
                "email": "",
                "password": ""
            }
            """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
    @Test
    void login_withInvalidCredentials_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest(
                "ajay@example.com",
                "wrongpassword"
        );

        when(userService.login(request))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

}