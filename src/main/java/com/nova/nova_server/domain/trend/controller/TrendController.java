package com.nova.nova_server.domain.trend.controller;

import com.nova.nova_server.domain.trend.dto.TrendResponse;
import com.nova.nova_server.domain.trend.service.TrendService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trends")
public class TrendController implements com.nova.nova_server.domain.trend.docs.TrendControllerDocs {

    private final TrendService trendService;

    @GetMapping("/keywords/keywordtop")
    public ApiResponse<TrendResponse> getTopKeywords() {
        return ApiResponse.success(trendService.getTopKeywords(LocalDate.now()));
    }

    @GetMapping("/interests/skilltop")
    public ApiResponse<com.nova.nova_server.domain.trend.dto.SkillTrendResponse> getSkillTrend() {
        return ApiResponse.success(trendService.getSkillTrend());
    }
}