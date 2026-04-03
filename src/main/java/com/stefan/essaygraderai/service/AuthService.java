package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.request.RegisterRequest;
import com.stefan.essaygraderai.dto.response.AuthResponse;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.Role;
import com.stefan.essaygraderai.exception.EmailAlreadyExistsException;
import com.stefan.essaygraderai.repository.UserRepository;
import com.stefan.essaygraderai.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<AuthResponse> register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        AuthResponse authResponse = new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());

        return ResponseEntity.ok(authResponse);

    }
}
