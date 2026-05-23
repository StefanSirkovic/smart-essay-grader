package com.stefan.essaygraderai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.essaygraderai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EssayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createEssay_shouldReturn201_whenAuthenticated() throws Exception {
        String registerJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        String createEssayJson = """
                {
                    "title" : "My First Essay",
                    "text" : "This is a test essay that needs to be at least fifty characters long to pass validation and kafka tests."
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = extractToken(result);


        mockMvc.perform(post("/api/essays")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEssayJson))
                .andExpect(status().isCreated());

    }

    @Test
    void createEssay_shouldReturn401_whenNoToken() throws Exception {
        String createEssayJson = """
                {
                    "title" : "My First Essay",
                    "text" : "This is a test essay that needs to be at least fifty characters long to pass validation and kafka tests."
                }
                """;

        mockMvc.perform(post("/api/essays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEssayJson))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getMyEssays_shouldReturn200() throws Exception {

        String registerJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        String createEssayJson = """
                {
                    "title" : "My First Essay",
                    "text" : "This is a test essay that needs to be at least fifty characters long to pass validation and kafka tests."
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = extractToken(result);

        mockMvc.perform(post("/api/essays")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEssayJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/essays")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEssay_shouldReturn204() throws Exception {

        String registerJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        String createEssayJson = """
                {
                    "title" : "My First Essay",
                    "text" : "This is a test essay that needs to be at least fifty characters long to pass validation and kafka tests."
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = extractToken(result);

        MvcResult createResult = mockMvc.perform(post("/api/essays")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEssayJson))
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String response = createResult.getResponse().getContentAsString();
        Long essayId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/essays/" + essayId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private String extractToken(MvcResult result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token")
                .asText();
    }


}
