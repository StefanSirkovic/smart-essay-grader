package com.stefan.essaygraderai.controller;


import com.stefan.essaygraderai.config.TestConfig;
import com.stefan.essaygraderai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturn201_whenValid() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@test.com"));

    }

    @Test
    void register_shouldReturn409_whenEmailExists() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

    }

    @Test
    void register_shouldReturn400_whenInvalidEmail() throws Exception {
        String invalidEmailJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "testtest.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void login_shouldReturn200_whenCredentialsValid() throws Exception {
        String registerJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        String loginJson = """
                {
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@test.com"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }

    @Test
    void login_shouldReturn401_whenWrongPassword() throws Exception {
        String registerJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        String loginJsonWrongPassword = """
                {
                    "email": "test@test.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@test.com"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJsonWrongPassword))
                .andExpect(status().isUnauthorized());
    }


}
