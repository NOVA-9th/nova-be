package com.nova.nova_server.domain.batch.summary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * AI 요약 결과를 담는 데이터 객체
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LlmSummaryResult(
        @JsonProperty(required = true) String summary,
        @JsonProperty(required = true) List<String> evidence,
        @JsonProperty(required = true) List<String> keywords
) {
}
