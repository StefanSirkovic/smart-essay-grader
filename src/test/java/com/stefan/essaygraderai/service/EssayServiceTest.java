package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.request.EssayRequest;
import com.stefan.essaygraderai.dto.response.EssayResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.enums.Role;
import com.stefan.essaygraderai.exception.EssayAlreadyGradedException;
import com.stefan.essaygraderai.exception.EssayNotFoundException;
import com.stefan.essaygraderai.repository.EssayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EssayServiceTest {

    @Mock
    private EssayRepository essayRepository;

    @InjectMocks
    private EssayService essayService;

    private User user;
    private Essay essay;
    private EssayRequest request;

    @BeforeEach
    void setUp() {
        user = createTestUser();
        request = createTestEssay();

        essay = new Essay();
        essay.setId(1L);
        essay.setUser(user);
    }

    @Test
    void createEssay_shouldReturnEssayResponse() {

        EssayResponse result = essayService.createEssay(request, user);

        assertNotNull(result);
        assertEquals(EssayStatus.SUBMITTED, result.status());
        verify(essayRepository).save(any(Essay.class));

    }

    @Test
    void getEssayById_shouldReturnEssay_whenFound() {

        when(essayRepository.findByIdAndUserId(essay.getId(), user.getId())).thenReturn(Optional.of(essay));

        EssayResponse result = essayService.getEssayById(essay.getId(), user);

        assertEquals(1L, result.id());
        assertNotNull(result);

    }

    @Test
    void getEssayById_shouldThrowException_whenNotFound() {

        when(essayRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(EssayNotFoundException.class,
                () -> essayService.getEssayById(essay.getId(), user));

    }

    @Test
    void updateEssay_shouldThrowException_whenEssayAlreadyGraded() {
        essay.setEssayStatus(EssayStatus.GRADED);
        when(essayRepository.findByIdAndUserId(essay.getId(), user.getId())).thenReturn(Optional.of(essay));

        assertThrows(EssayAlreadyGradedException.class,
                () -> essayService.updateEssay(essay.getId(), request, user));
    }

    @Test
    void deleteEssay_shouldCallDelete_whenEssayExists() {
        when(essayRepository.findByIdAndUserId(essay.getId(), user.getId())).thenReturn(Optional.of(essay));

        essayService.deleteEssay(essay.getId(), user);

        verify(essayRepository).delete(essay);

    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(Role.STUDENT);
        return user;
    }

    private EssayRequest createTestEssay() {
        return new EssayRequest("My First Essay",
                "This is a test essay that needs to be at least fifty " +
                        "characters long to pass validation and kafka tests.");
    }


}
