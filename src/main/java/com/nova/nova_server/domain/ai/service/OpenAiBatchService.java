package com.nova.nova_server.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nova.nova_server.domain.ai.exception.AiException;
import com.nova.nova_server.global.config.OpenAIConfig;
import com.openai.client.OpenAIClient;
import com.openai.core.MultipartField;
import com.openai.core.http.HttpResponse;
import com.openai.models.batches.Batch;
import com.openai.models.batches.BatchCreateParams;
import com.openai.models.batches.BatchCreateParams.CompletionWindow;
import com.openai.models.batches.BatchCreateParams.Endpoint;
import com.openai.models.files.FileCreateParams;
import com.openai.models.files.FileObject;
import com.openai.models.files.FilePurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.openai.models.batches.Batch.Status.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiBatchService implements AiBatchService {

    private final OpenAIClient client;
    private final OpenAIConfig config;
    private final ObjectMapper objectMapper;

    @Override
    public String createBatch(List<String> prompts) {
        validatePrompts(prompts);

        String batchInput = createBatchInput(
                prompts,
                config.getModel(),
                config.getTemperature()
        );
        String inputFileId = uploadBatchInput(batchInput);

        return requestBatch(inputFileId);
    }

    @Override
    public boolean isCompleted(String batchId) {
        validateBatchId(batchId);

        Batch batch = client.batches().retrieve(batchId);

        return isCompleted(batch);
    }

    @Override
    public List<String> getResultList(String batchId) {
        Batch batch = client.batches().retrieve(batchId);
        if (!isCompleted(batch)) {
            throw new AiException.PendingBatchException("배치 작업이 아직 완료되지 않았습니다. batchId: " + batchId);
        }

        String batchOutput = fetchBatchOutput(batch);

        return parseBatchOutput(batchOutput);
    }

    /**
     * OpenAI 배치 작업에 필요한 jsonl 형식의 입력 문자열을 생성한다.
     *
     * @param prompts     프롬프트 리스트
     * @param model       OpenAI LLM 모델 이름
     * @param temperature temperature 값
     * @return 배치 입력 문자열 (jsonl 형식)
     */
    private String createBatchInput(List<String> prompts, String model, double temperature) {
        StringBuilder jsonlBuilder = new StringBuilder();

        for (int i = 0; i < prompts.size(); i++) {
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("custom_id", String.valueOf(i + 1));
            requestNode.put("method", "POST");
            requestNode.put("url", "/v1/chat/completions");

            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("model", model);
            bodyNode.put("temperature", temperature);

            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompts.get(i));

            bodyNode.putArray("messages").add(messageNode);
            requestNode.set("body", bodyNode);

            jsonlBuilder.append(requestNode).append("\n");
        }

        return jsonlBuilder.toString();
    }

    /**
     * 배치 입력 파일을 업로드한다.
     *
     * @param batchInput 배치 입력 문자열 (jsonl 형식)
     * @return 업로드된 파일 ID
     */
    private String uploadBatchInput(String batchInput) {
        FileObject file = client.files().create(
                FileCreateParams.builder()
                        .purpose(FilePurpose.BATCH)
                        .file(MultipartField.<InputStream>builder()
                                .value(new ByteArrayInputStream(batchInput.getBytes(StandardCharsets.UTF_8)))
                                .filename("batch_input.jsonl").build())
                        .build()
        );

        return file.id();
    }

    /**
     * 배치 작업을 요청한다.
     *
     * @param inputFileId 업로드된 배치 입력 파일 ID
     * @return 배치 작업 ID
     */
    private String requestBatch(String inputFileId) {
        Batch batch = client.batches().create(
                BatchCreateParams.builder()
                        .inputFileId(inputFileId)
                        .endpoint(Endpoint.V1_CHAT_COMPLETIONS)
                        .completionWindow(CompletionWindow._24H)
                        .build()
        );

        return batch.id();
    }

    /**
     * 배치 작업 완료 여부를 확인한다.
     * 배치 입력 검증에 실패한 경우 InvalidBatchInputException을 던진다.
     *
     * @param batch 배치 작업 객체
     * @return 완료 여부
     */
    private boolean isCompleted(Batch batch) {
        Batch.Status status = batch.status();

        // Expired, Cancelled라도 일부 항목이 완료되었을 수 있으므로 이후 단계 수행
        if (status.equals(COMPLETED)
                || status.equals(EXPIRED)
                || status.equals(CANCELLED))
            return true;
        if (status.equals(FAILED))
            throw new AiException.InvalidBatchInputException("배치 입력 검증에 실패했습니다. batchId: " + batch.id());

        return false;
    }

    /**
     * 배치 작업 결과를 가져온다.
     *
     * @param batch 배치 작업 객체
     * @return 배치 작업 결과 문자열 (jsonl 형식)
     */
    private String fetchBatchOutput(Batch batch) {
        StringBuffer outputBuffer = new StringBuffer();

        batch.outputFileId().ifPresent(fileId -> {
            outputBuffer.append(fetchBatchOutputFile(fileId));
        });
        batch.errorFileId().ifPresent(fileId -> {
            outputBuffer.append(fetchBatchOutputFile(fileId));
        });

        return outputBuffer.toString();
    }

    /**
     * 배치 작업 결과 파일을 가져온다.
     *
     * @param fileId 배치 결과 파일 ID
     * @return 배치 작업 결과 문자열 (jsonl 형식)
     */
    private String fetchBatchOutputFile(String fileId) {
        try (HttpResponse response = client.files().content(fileId)) {
            return new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AiException.InvalidBatchOutputException("배치 결과 파일을 읽는 중 오류가 발생했습니다. fileId: " + fileId);
        }
    }

    /**
     * 배치 작업 결과를 파싱해서 응답을 요청 순서와 동일하게 정렬하여 반환한다.
     * 상태가 Expired인 경우 미완료된 요청이 포함될 수 있으며, 이 경우 null로 처리한다.
     *
     * @param batchOutput 배치 작업 결과 문자열 (jsonl 형식)
     * @return 배치 응답 리스트
     */
    private List<String> parseBatchOutput(String batchOutput) {
        Map<Integer, String> resultMap = new TreeMap<>();
        int maxCustomId = 0;

        String[] lines = batchOutput.split("\n");
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            try {
                JsonNode resultNode = objectMapper.readTree(line);
                String customId = resultNode.get("custom_id").asText();
                int requestNumber = Integer.parseInt(customId);
                maxCustomId = Math.max(maxCustomId, requestNumber);

                String content = resultNode.at("/response/body/choices/0/message/content").asText();
                resultMap.put(requestNumber, content);

            } catch (Exception e) {
                log.warn("배치 결과 파싱 중 오류가 발생했습니다.\n{}", e.getMessage());
            }
        }

        List<String> resultList = new java.util.ArrayList<>();
        for (int i = 1; i <= maxCustomId; i++) {
            resultList.add(resultMap.getOrDefault(i, null));
        }

        return resultList;
    }

    private void validatePrompts(List<String> prompts) {
        if (CollectionUtils.isEmpty(prompts))
            throw new AiException.InvalidBatchInputException("배치 입력이 누락되었습니다.");
        if (prompts.size() > config.getMaxRequestPerBatch())
            throw new AiException.InvalidBatchInputException("배치 당 최대 요청수를 초과했습니다.");
    }

    private void validateBatchId(String batchId) {
        if (!StringUtils.hasText(batchId))
            throw new AiException.InvalidBatchIdException("배치 ID가 누락되었습니다.");
    }

}
