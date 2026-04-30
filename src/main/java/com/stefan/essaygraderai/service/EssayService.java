package com.stefan.essaygraderai.service;

import com.stefan.essaygraderai.dto.request.EssayRequest;
import com.stefan.essaygraderai.dto.response.EssayResponse;
import com.stefan.essaygraderai.entity.Essay;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.enums.EssayStatus;
import com.stefan.essaygraderai.exception.EssayAlreadyGradedException;
import com.stefan.essaygraderai.exception.EssayNotFoundException;
import com.stefan.essaygraderai.repository.EssayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EssayService {

    private final EssayRepository essayRepository;

    public EssayService(EssayRepository essayRepository) {
        this.essayRepository = essayRepository;
    }

    public EssayResponse createEssay(EssayRequest request, User currentUser) {
        Essay essay = Essay.builder()
                .title(request.title())
                .text(request.text())
                .essayStatus(EssayStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .user(currentUser)
                .build();

        essayRepository.save(essay);

        return mapToResponse(essay);
    }

    public List<EssayResponse> getMyEssays(User currentUser) {
        return essayRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EssayResponse getEssayById(Long id, User currentUser) {
        Essay essay = essayRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));

        return mapToResponse(essay);

    }

    public EssayResponse updateEssay(Long id, EssayRequest request, User currentUser) {
        Essay essay = essayRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));

        if (essay.getEssayStatus() != EssayStatus.SUBMITTED) {
            throw new EssayAlreadyGradedException("Essay cannot be edited after grading");
        }

        essay.setTitle(request.title());
        essay.setText(request.text());

        essayRepository.save(essay);

        return mapToResponse(essay);
    }

    public void deleteEssay(Long id, User currentUser) {
        Essay essay = essayRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new EssayNotFoundException("Essay not found."));

        essayRepository.delete(essay);

    }

    private EssayResponse mapToResponse(Essay essay) {
        return new EssayResponse(
                essay.getId(),
                essay.getTitle(),
                essay.getText(),
                essay.getEssayStatus(),
                essay.getSubmittedAt(),
                essay.getUser().getId(),
                essay.getUser().getEmail()
        );
    }
}
