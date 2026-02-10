package com.nova.nova_server.domain.batch.cardnews.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.nova_server.domain.batch.summary.dto.LlmSummaryResult;

import java.util.HashMap;
import java.util.Map;

public class AiResponseConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<Long, LlmSummaryResult> fromBatchResult(Map<String, String> batchResult) {
        Map<Long, LlmSummaryResult> mapResult = new HashMap<>();

        for (Map.Entry<String, String> entry : batchResult.entrySet()) {
            Long articleId = Long.parseLong(entry.getKey());
            String summaryJson = entry.getValue();

            if (summaryJson == null) {
                mapResult.put(articleId, null);
            }
            else {
                LlmSummaryResult summary = parseLlmResult(summaryJson);
                mapResult.put(articleId, summary);
            }
        }

        return mapResult;
    }

    private static LlmSummaryResult parseLlmResult(String llmJson) {
        try {
            // LLM 응답에서 JSON 부분만 추출 (마크다운 코드 블럭 등 제거)
            String json = extractJson(llmJson);
            return objectMapper.readValue(json, LlmSummaryResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM result: " + llmJson, e);
        }
    }

    private static String extractJson(String content) {
        // ```json ... ``` 형태로 감싸져 있을 수 있음
        if (content.contains("```")) {
            int start = content.indexOf("{");
            int end = content.lastIndexOf("}");
            if (start >= 0 && end > start) {
                return content.substring(start, end + 1);
            }
        }
        return content.trim();
    }
}
