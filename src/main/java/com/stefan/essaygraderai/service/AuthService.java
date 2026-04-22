package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.request.LoginRequest;
import com.stefan.essaygraderai.dto.request.RegisterRequest;
import com.stefan.essaygraderai.dto.response.AuthResponse;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.Role;
import com.stefan.essaygraderai.exception.EmailAlreadyExistsException;
import com.stefan.essaygraderai.repository.UserRepository;
import com.stefan.essaygraderai.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest registerRequest) {

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

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());

    }

    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());

    }
}
