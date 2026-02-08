지금까지 논의한 **Spring Batch 기반 AI 요약 시스템**의 설계 사양을 다른 LLM이나 개발자가 즉시 이해하고 구현할 수 있도록 기술 명세서 형식으로 정리해 드립니다.

---

# 기술 명세: Spring Batch 기반 병렬 데이터 수집 및 AI 배치 요약 시스템

## 1. 개요 (Overview)

본 시스템은 다양한 소스(Blog, News, SNS 등)로부터 데이터를 병렬로 수집하여 전처리한 후, 임시 스테이징 테이블에 저장하고, 이를 다시 대량(Chunk)으로 묶어 AI API로 요약 처리하는 2단계 배치 아키텍처를 가집니다.

---

## 2. 데이터 모델 (JPA Entities)

### 2.1. StagingEntity (임시 저장소)

* **Table Name:** `post_staging`
* **Fields:**
* `Long id` (PK)
* `String sourceType` (BLOG, NEWS, SNS 등)
* `String rawUrl` (원본 URL)
* `Lob String content` (전처리된 본문 텍스트)
* `Boolean isProcessed` (AI 요약 완료 여부, 기본값 false)
* `LocalDateTime createdAt`



### 2.2. SummaryEntity (최종 저장소)

* **Table Name:** `post_summary`
* **Fields:**
* `Long id` (PK)
* `Long stagingId` (FK, 원본 참조)
* `Lob String summary` (AI 생성 요약본)
* `LocalDateTime processedAt`



---

## 3. 배치 아키텍처 (2-Phase Architecture)

### Phase 1: Parallel Data Ingestion (병렬 수집 및 전처리)

* **목적:** 다양한 소스로부터 데이터를 동시에 긁어와서 DB에 규격화된 형태로 적재.
* **구조:** `Split`을 사용하여 여러 개의 `Flow`를 병렬 실행.
* **구성 요소:**
* **ItemReader:** 소스별(API, RSS, DB 등) 커스텀 리더 구현.
* **ItemProcessor:** 실제 콘텐츠 다운로드(I/O), HTML 태그 제거, 텍스트 정규화.
* **ItemWriter:** `StagingEntity`를 `post_staging` 테이블에 저장.


* **성능 최적화:** `TaskExecutor`를 사용하여 멀티스레드로 각 Step 내부의 Chunk 처리 가속화.

### Phase 2: AI Batch Summarization (AI 배치 요약)

* **목적:** 스테이징된 데이터를 AI API의 배치 호출 단위(Chunk)로 묶어 효율적으로 처리.
* **구조:** 단일 Step, Chunk 기반 프로세싱.
* **구성 요소:**
* **ItemReader:** `RepositoryItemReader`를 사용하여 `isProcessed = false`인 데이터 조회.
* **ItemProcessor:** (선택적) AI 모델에 맞는 토큰 제한 확인 및 포맷팅.
* **ItemWriter (핵심):** 1. 전달받은 `List<StagingEntity>`(Chunk 단위, 예: 1000개)를 추출.
2. AI 배치 API를 단일 호출하여 1000개의 요약 결과 수신.
3. 결과를 `SummaryEntity`에 저장하고, `StagingEntity`의 `isProcessed`를 `true`로 업데이트.


* **성능 최적화:** AI API의 최대 수용량과 Chunk 사이즈를 일치시킴 (예: 1,000건).

---

## 4. 상세 구현 가이드 (Logic details)

### 4.1. 병렬 실행 설정 (Parallel Flow)

```java
// Job 설정 예시
@Bean
public Job aiJob() {
    return new JobBuilder("aiJob", jobRepository)
        .start(parallelCollectionFlow()) // Step 1-A, 1-B... 병렬 실행
        .next(aiProcessingStep())        // Step 2 순차 실행
        .build();
}

```

### 4.2. AI 호출 전략 (Writer-based Batch)

* **이유:** `ItemProcessor`는 1건씩 작동하므로 네트워크 오버헤드가 크지만, `ItemWriter`는 Chunk 전체(`List<T>`)에 접근 가능하므로 Batch API 호출에 적합함.
* **트랜잭션:** 한 번의 AI API 호출 단위와 DB 커밋 단위를 Chunk 사이즈로 일치시켜 데이터 정합성 유지.

---

## 5. 예외 처리 및 안정성 (Fault Tolerance)

* **Skip:** AI API 호출 시 특정 데이터에서 에러가 날 경우 해당 건만 건너뛰도록 `faultTolerant().skip()` 설정.
* **Restart:** 1단계에서 수집이 중단되더라도 `isProcessed` 플래그를 통해 2단계에서 실패 지점부터 재시작 가능.
* **Rate Limiting:** AI API의 속도 제한에 따라 `ThrottleLimit`을 설정하여 서버 부하 조절.

---

이 내용을 바탕으로 구현을 시작하시면 됩니다. **다음 단계로 이 설계에 기반한 실제 Spring Boot 환경의 Java 설정 코드(JobConfig)가 필요하신가요?** 혹은 특정 파트의 상세 로직 구현이 궁금하신가요?


# TODO

- 500자 넘는 글 ... 줄이기
- 컨텐츠 없으면 NO CONTENT 같은거 추가하기
- 빈 데이터, 빈 소스는 skip 하기, 근데 중복 수집되면 안됨
- 하나의 client 에서 여러 입력 넣을수있게
- ArticleSource 에서 몇개만 fetch 할지 전략, 중복 처리도 ArticleSource 를 통해서