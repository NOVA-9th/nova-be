package com.nova.nova_server.domain.ai.service;

import java.util.Map;

public interface AiBatchService {

    /**
     * 배치 작업을 요청한다.
     *
     * @param prompts 임의의 ID를 key로, prompt 내용을 value로 갖는 Map<br/>
     *                Batch API 결과는 순서가 보장되지 않으므로 구분을 위해 prompt마다 고유한 ID를 부여해야 한다.
     * @return 배치 작업 ID
     */
    String createBatch(Map<String, String> prompts);

    /**
     * 배치 작업 완료 여부를 확인한다.
     * 배치 작업에 실패한 경우 AiException 하위 예외를 던진다.
     *
     * @param batchId 배치 작업 ID
     * @return 완료 여부
     * @throws com.nova.nova_server.domain.ai.exception.AiException
     */
    boolean isCompleted(String batchId);

    /**
     * 배치 작업의 응답을 가져온다.
     *
     * @param batchId 배치 작업 ID
     * @return 요청에서 지정된 ID를 key로, LLM 응답을 value로 갖는 Map
     */
    Map<String, String> getResults(String batchId);

}
