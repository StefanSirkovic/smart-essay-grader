package com.stefan.essaygraderai.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.stefan.essaygraderai.dto.request.GeminiRequest;
import com.stefan.essaygraderai.dto.response.GeminiResponse;
import com.stefan.essaygraderai.dto.response.GradeResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.exception.GradingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class AiService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    public GradeResponse getData(Essay essay) {

        String prompt = """
                Return ONLY valid JSON:
                {
                  "score": number (0-100),
                  "feedback": "detailed feedback"
                }
                
                Essay:
                """ + essay.getText();

        GeminiRequest request = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        GeminiResponse response = restClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        String responseText = Optional.ofNullable(response)
                .map(GeminiResponse::candidates)
                .filter(c -> !c.isEmpty())
                .map(c -> c.get(0))
                .map(GeminiResponse.Candidate::content)
                .map(GeminiResponse.Content::parts)
                .filter(p -> !p.isEmpty())
                .map(p -> p.get(0))
                .map(GeminiResponse.Part::text)
                .orElseThrow(() -> new GradingException("Invalid Gemini response"));

        responseText = responseText.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        try {
            return objectMapper.readValue(responseText, GradeResponse.class);
        } catch (JsonProcessingException e) {
            throw new GradingException("Failed to parse AI response: " + responseText);
        }
    }
}