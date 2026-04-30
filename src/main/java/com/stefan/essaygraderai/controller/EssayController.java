package com.stefan.essaygraderai.controller;

import com.stefan.essaygraderai.dto.request.EssayRequest;
import com.stefan.essaygraderai.dto.response.EssayResponse;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.service.EssayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/essays")
public class EssayController {

    private final EssayService essayService;

    public EssayController(EssayService essayService) {
        this.essayService = essayService;
    }

    @PostMapping
    public ResponseEntity<EssayResponse> create(@Valid @RequestBody EssayRequest request,
                                                @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(essayService.createEssay(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<EssayResponse>> getMyEssays(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok().body(essayService.getMyEssays(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EssayResponse> getById(@PathVariable Long id,
                                                 @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok().body(essayService.getEssayById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EssayResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody EssayRequest request,
                                                @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok().body(essayService.updateEssay(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal User currentUser) {
        essayService.deleteEssay(id, currentUser);
        return ResponseEntity.noContent().build();
    }

}
