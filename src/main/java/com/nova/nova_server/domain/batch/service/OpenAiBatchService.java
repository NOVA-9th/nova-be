package com.nova.nova_server.domain.batch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAiBatchService implements AiBatchService {

    private static final String OPENAI_BASE_URL = "https://api.openai.com";
    private static final String CHAT_COMPLETIONS_URL = "/v1/chat/completions";

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiBatchService(
            WebClient.Builder webClientBuilder,
            @Value("${ai.openai.token}") String token,
            @Value("${ai.openai.model:gpt-4o-mini}") String model) {
        this.webClient = webClientBuilder
                .baseUrl(OPENAI_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        this.model = model;
    }

    @Override
    public String createBatch(List<String> promptWithArticles) {
        if (promptWithArticles == null || promptWithArticles.isEmpty()) {
            throw new IllegalArgumentException("promptWithArticles cannot be empty");
        }

        List<String> jsonlLines = new ArrayList<>();
        for (int i = 0; i < promptWithArticles.size(); i++) {
            String customId = "article-" + i;
            String userContent = promptWithArticles.get(i);
            String requestBody = buildChatCompletionRequestBody(userContent);
            String jsonlLine = String.format("{\"custom_id\":\"%s\",\"method\":\"POST\",\"url\":\"%s\",\"body\":%s}",
                    customId, CHAT_COMPLETIONS_URL, requestBody);
            jsonlLines.add(jsonlLine);
        }

        String jsonlContent = String.join("\n", jsonlLines);

        // 1. Files API - 업로드
        String fileId = uploadFile(jsonlContent);

        // 2. Batches API - 배치 생성
        String batchId = createBatchRequest(fileId);
        log.info("OpenAI Batch created: batchId={}, requestCount={}", batchId, promptWithArticles.size());
        return batchId;
    }

    private String buildChatCompletionRequestBody(String userContent) {
        String escapedContent = userContent.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return String.format("{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":\"당신은 기사를 요약하고 핵심 정보를 추출하는 전문가입니다.\"},{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":1000}",
                model, escapedContent);
    }

    private String uploadFile(String jsonlContent) {
        byte[] bytes = jsonlContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return "batch_input.jsonl";
            }
        };

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", resource);
        formData.add("purpose", "batch");

        String response = webClient.post()
                .uri("/v1/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = parseJson(response);
        return root.get("id").asText();
    }

    private String createBatchRequest(String fileId) {
        String requestBody = String.format(
                "{\"input_file_id\":\"%s\",\"endpoint\":\"%s\",\"completion_window\":\"24h\"}",
                fileId, CHAT_COMPLETIONS_URL);

        String response = webClient.post()
                .uri("/v1/batches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = parseJson(response);
        return root.get("id").asText();
    }

    @Override
    public boolean isCompleted(String batchId) {
        String response = webClient.get()
                .uri("/v1/batches/{batchId}", batchId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = parseJson(response);
        String status = root.get("status").asText();

        if ("completed".equals(status) || "failed".equals(status) || "expired".equals(status) ||
                "cancelled".equals(status) || "canceled".equals(status)) {
            log.info("OpenAI Batch finished: batchId={}, status={}", batchId, status);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> fetchResults(String batchId) {
        String batchResponse = webClient.get()
                .uri("/v1/batches/{batchId}", batchId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode batchRoot = parseJson(batchResponse);
        JsonNode outputFileIdNode = batchRoot.get("output_file_id");
        if (outputFileIdNode == null || outputFileIdNode.isNull()) {
            log.warn("OpenAI Batch has no output file: batchId={}", batchId);
            return new HashMap<>();
        }

        String outputFileId = outputFileIdNode.asText();

        byte[] content = webClient.get()
                .uri("/v1/files/{fileId}/content", outputFileId)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (content == null || content.length == 0) {
            return new HashMap<>();
        }

        String contentStr = new String(content, StandardCharsets.UTF_8);
        Map<String, String> results = new HashMap<>();

        for (String line : contentStr.split("\n")) {
            if (line.isBlank()) continue;
            try {
                JsonNode root = objectMapper.readTree(line);
                String customId = root.has("custom_id") ? root.get("custom_id").asText() : null;
                if (customId == null) continue;

                JsonNode responseNode = root.get("response");
                if (responseNode == null || responseNode.isNull()) {
                    results.put(customId, null);
                    continue;
                }

                JsonNode body = responseNode.get("body");
                if (body == null || body.isNull()) {
                    results.put(customId, null);
                    continue;
                }

                JsonNode choices = body.get("choices");
                if (choices == null || !choices.isArray() || choices.isEmpty()) {
                    results.put(customId, null);
                    continue;
                }

                JsonNode message = choices.get(0).get("message");
                if (message == null || !message.has("content")) {
                    results.put(customId, null);
                    continue;
                }

                String llmContent = message.get("content").asText();
                results.put(customId, llmContent);
            } catch (Exception e) {
                log.warn("Failed to parse batch output line: {}", line, e);
            }
        }
        return results;
    }

    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + json, e);
        }
    }
}
