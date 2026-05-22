package com.stefan.essaygraderai.controller;

import com.stefan.essaygraderai.dto.response.DashboardResponse;
import com.stefan.essaygraderai.entity.User;
import com.stefan.essaygraderai.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(dashboardService.getDashboard(currentUser));
    }

}
