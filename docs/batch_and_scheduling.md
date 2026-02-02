이 문서는 NOVA 서버의 카드 뉴스 자동 생성 및 배치(Batch) 작업 루키, 스케줄링 구현 내용에 대해 설명합니다.

---

### 1. 전체 아키텍처 (Workflow)

배치 작업은 '수집 -> 요약 및 분석 -> 저장'의 3단계로 이루어지며, OpenAI의 **Batch API**를 사용하여 비용 효율적으로 대량의 데이터를 처리합니다.

1.  **Article Ingestion (수집):** 10개 이상의 외부 뉴스 및 커뮤니티 API(Hacker News, GitHub, Naver News 등)에서 기사 데이터를 긁어옵니다. (각 API당 최대 10개)
2.  **LLM Batch Processing (분석):** 수집된 기사를 바탕으로 OpenAI Batch API용 JSONL 파일을 생성하여 전송합니다.
3.  **Persistence (저장):** AI 분석 결과(요약, 근거, 키워드)를 파싱하여 `card_news` 테이블에 저장합니다.


---

### 2. 주요 기능 및 제약 사항

*   **증분 수집 (Incremental Fetching):** `batch_run_metadata` 테이블을 참조하여 마지막 배치 성공 시점 이후에 발행된 기사만 새롭게 수집합니다.
*   **중복 방지 (Duplicate Detection):** `original_url` 필드를 기준으로 이미 DB에 존재하는 기사는 수집 대상에서 제외합니다.
*   **격리된 클라이언트 (Isolated WebClient):** 각 외부 API 호출 시 공통 `WebClient.Builder`를 클론하여 사용하여, 서비스 간 설정(인증 토큰 등) 오염을 방지합니다.
*   **개별 트랜잭션 (Atomic Persistence):** 기사 저장 시 개별 트랜잭션을 적용하여, 특정 기사의 요약 결과가 비정상적이더라도 다른 정상적인 기사들은 안전하게 저장될 수 있도록 설계되었습니다.

---

### 3. API 명세 및 실행 방법

#### 수동 배치 실행
스케줄러와 별개로 즉각적인 배치가 필요할 때 사용합니다.

*   **URL:** `/batch/card-news/execute`
*   **Method:** **POST**
*   **설명:** 기사 수집부터 AI 요약, DB 저장까지의 전체 프로세스를 즉시 실행합니다. (Batch API 특성상 최종 저장까지 수 분이 소요될 수 있습니다.)

#### 디버깅용 API (Admin/Debug 전용)
*   `GET /debug/news/latest-metadata`: 마지막 배치 실행 결과 확인
*   `GET /debug/news/db-count`: 현재 저장된 전체 카드 뉴스 개수 확인

---

### 4. 스케줄링 설정

배치 작업은 Spring의 `@Scheduled`를 사용하여 자동 실행됩니다.

*   **설정 파일:** `CardNewsBatchScheduler.java`
*   **주기:** 현재 **30분 단위**(0 0/30 * * * *)로 설정되어 있습니다. (프로젝트 설정에 따라 변경 가능)

---

### 5. 관련 테이블 정보

*   `card_news`: 생성된 카드 뉴스의 전문, 요약, 발행 정보를 담는 메인 테이블
*   `batch_run_metadata`: 배치의 성공/실패 여부와 증분 수집의 기준이 되는 실행 시각을 기록하는 테이블
*   `keyword` & `card_news_keyword`: AI가 추출한 핵심 키워드 매핑 테이블

---

### 6. 모니터링 및 트러블슈팅

*   **OpenAI 상태 확인:** `OpenAiBatchService` 로그에서 `batchId`를 확인하여 OpenAI 대시보드에서 진행 상황을 모니터링할 수 있습니다.
*   **데이터 필터링 확인:** 기사가 수집되지 않는다면 `isAfterLastRun` 로직에 의해 '최신 글이 없음'으로 판정되었거나, 이미 DB에 있는 URL인지 확인하십시오.

### openai 부분은 수정 필요