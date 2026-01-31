package com.nova.nova_server.domain.batch.service;

import java.util.List;
import java.util.Map;

/**
 * OpenAI Batch API를 통한 비동기 요약 생성 서비스
 * - createBatch: 프롬프트+아티클 목록을 받아 배치 생성 후 배치 ID 반환
 * - isCompleted: 배치 완료 여부 폴링 (completed/failed 포함)
 * - fetchResults: 배치 결과 조회 (custom_id → LLM 응답 매핑)
 */
public interface AiBatchService {

    /**
     * 프롬프트와 아티클을 조합한 문자열 목록으로 배치 생성
     *
     * @param promptWithArticles "프롬프트" + article.toString() 형태의 문자열 목록
     * @return OpenAI Batch ID
     */
    String createBatch(List<String> promptWithArticles);

    /**
     * 배치 완료 여부 확인
     * completed 또는 failed(일부 성공 가능) 시 true 반환
     * validating, in_progress 등 진행 중일 때 false 반환
     *
     * @param batchId OpenAI Batch ID
     * @return 완료 여부 (true: 다음 스텝 진행 가능)
     */
    boolean isCompleted(String batchId);

    /**
     * 배치 결과 조회
     *
     * @param batchId OpenAI Batch ID
     * @return custom_id → LLM 응답 JSON 문자열 매핑 (실패 케이스는 null)
     */
    Map<String, String> fetchResults(String batchId);
}
