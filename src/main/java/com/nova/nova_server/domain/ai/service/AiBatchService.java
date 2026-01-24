package com.nova.nova_server.domain.ai.service;

import java.util.List;

public interface AiBatchService {

    /**
     * 배치 작업을 요청한다.
     *
     * @param prompts 프롬프트 리스트
     * @return 배치 작업 ID
     */
    String createBatch(List<String> prompts);

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
     * @return 결과 리스트
     */
    List<String> getResultList(String batchId);

}
