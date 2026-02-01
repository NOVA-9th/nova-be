package com.nova.nova_server.domain.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * AI 요약 결과를 담는 데이터  객체
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LlmSummaryResult(
        String summary,
        String evidence,
        List<String> keywords) {
}
