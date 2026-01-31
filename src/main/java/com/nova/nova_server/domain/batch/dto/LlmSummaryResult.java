package com.nova.nova_server.domain.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * LLM 요약 결과 DTO
 * {"summary":"...", "evidence":"...", "keywords":["...","...","...","...","..."]}
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmSummaryResult {
    private String summary;
    private String evidence;
    private List<String> keywords;
}
