package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.request.LoginRequest;
import com.stefan.essaygraderai.dto.request.RegisterRequest;
import com.stefan.essaygraderai.dto.response.AuthResponse;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.Role;
import com.stefan.essaygraderai.exception.EmailAlreadyExistsException;
import com.stefan.essaygraderai.repository.UserRepository;
import com.stefan.essaygraderai.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void register_shouldReturnAuthResponse_whenEmailIsNew() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@test.com", "password123");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        AuthResponse result = authService.register(request);

        assertNotNull(result.token());
        verify(userRepository).save(any(User.class));

    }

    @Test
    void register_shouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@test.com", "password123");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(request));

    }

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsValid() {
        LoginRequest request = new LoginRequest("test@test.com", "password123");

        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@test.com");
        user.setPassword("password123");
        user.setRole(Role.STUDENT);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        when(authentication.getPrincipal()).thenReturn(user);

        AuthResponse result = authService.login(request);

        assertNotNull(result.token());
        assertEquals("test@test.com", result.email());
        assertEquals(Role.STUDENT, result.role());
        verify(authenticationManager).authenticate(any());

    }


}
