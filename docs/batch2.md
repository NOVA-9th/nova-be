`ThreadPoolTaskExecutor`를 활용하여 **소스별 병렬 실행(Split)**과 **Step 내부의 병렬 처리(Multi-threaded Chunk)**를 모두 결합한 1단계 최종 코드를 작성해 드립니다.

이 구조는 "여러 소스를 동시에 긁으면서(Split), 각 소스 안에서도 여러 스레드가 본문을 다운로드(Multi-thread)"하는 가장 빠른 방식입니다.

---

### 1. ThreadPoolTaskExecutor 설정

스레드를 효율적으로 재사용하기 위해 빈(Bean)으로 등록합니다.

```java
@Configuration
public class ExecutorConfig {
    @Bean
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);    // 기본 스레드 10개
        executor.setMaxPoolSize(20);     // 최대 20개까지 확장
        executor.setQueueCapacity(50);   // 대기 큐
        executor.setThreadNamePrefix("Batch-Thread-");
        executor.initialize();
        return executor;
    }
}

```

---

### 2. 병렬 처리를 고려한 1단계 최종 코드 (JobConfig)

**주의:** 멀티스레드 환경에서는 `ItemReader`가 Thread-safe해야 합니다. 직접 만든 리더를 쓸 경우 `SynchronizedItemStreamReader`로 감싸줘야 안전합니다.

```java
@Configuration
@RequiredArgsConstructor
public class CollectionJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DataPreprocessingProcessor processor; // 이전 답변의 Processor
    private final TaskExecutor batchTaskExecutor;      // 위에서 만든 Executor 주입

    @Bean
    public Job parallelCollectionJob() {
        return new JobBuilder("parallelCollectionJob", jobRepository)
                .start(splitFlow()) // 1단계: 병렬 수집 (블로그, 뉴스 등 동시에)
                .build()
                .build();
    }

    @Bean
    public Flow splitFlow() {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(batchTaskExecutor) // (1) 소스별 병렬 실행
                .add(blogFlow(), newsFlow())
                .build();
    }

    // 블로그 소스 흐름
    @Bean
    public Flow blogFlow() {
        return new FlowBuilder<SimpleFlow>("blogFlow")
                .start(createStep("blogStep", blogReader()))
                .build();
    }

    // 뉴스 소스 흐름
    @Bean
    public Flow newsFlow() {
        return new FlowBuilder<SimpleFlow>("newsFlow")
                .start(createStep("newsStep", newsReader()))
                .build();
    }

    // 공통 Step 생성 (여기에 내부 병렬 로직 포함)
    private Step createStep(String name, ItemReader<RawData> reader) {
        return new StepBuilder(name, jobRepository)
                .<RawData, PostStaging>chunk(20, transactionManager)
                .reader(synchronizedReader(reader)) // (2) Thread-safe 보장
                .processor(processor)
                .writer(jpaItemWriter())
                .taskExecutor(batchTaskExecutor)    // (3) Step 내부 Chunk 처리 병렬화
                .throttleLimit(5)                   // 동시에 실행될 스레드 제한 (상대 서버 보호)
                .build();
    }

    // --- 보조 도구들 ---

    // 커스텀 리더를 멀티스레드에서 안전하게 사용하도록 래핑
    private SynchronizedItemStreamReader<RawData> synchronizedReader(ItemReader<RawData> reader) {
        return new SynchronizedItemStreamReaderBuilder<RawData>()
                .delegate(reader)
                .build();
    }

    @Bean
    public ItemWriter<PostStaging> jpaItemWriter() {
        return new JpaItemWriterBuilder<PostStaging>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemReader<RawData> blogReader() { return new CustomBlogReader(); }
    private ItemReader<RawData> newsReader() { return new CustomNewsReader(); }
}

```

---

### 3. 이 코드의 2중 병렬 구조 설명

이 코드는 두 가지 차원에서 병렬화를 수행합니다.

1. **Split 병렬화 (External):** `blogFlow`와 `newsFlow`가 서로 다른 스레드에서 동시에 시작됩니다. 블로그를 다 긁을 때까지 뉴스가 기다리지 않습니다.
2. **Chunk 병렬화 (Internal):** 하나의 Step(예: 블로그 수집) 안에서도 여러 스레드가 작동합니다.
* 스레드 A: 블로그 글 1~20번 다운로드 및 전처리
* 스레드 B: 블로그 글 21~40번 다운로드 및 전처리
* 이를 통해 단일 소스 내에서도 본문을 긁어오는 속도가 획기적으로 향상됩니다.



---

### 4. 실무 운용 팁

* **`throttleLimit` 설정:** `taskExecutor`를 설정하면 기본적으로 스레드 개수만큼 동시에 작업을 수행합니다. 수집 단계에서는 상대방 서버에 과부하를 주지 않도록 `throttleLimit`을 낮게(예: 3~5) 설정하는 것을 권장합니다.
* **DB 커넥션:** 병렬 개수가 많아지면 `HikariCP`의 `maximum-pool-size`를 그만큼 늘려주어야 합니다. (최소 스레드 총합 + 1~2개)

이제 1단계 수집 시스템이 완벽하게 갖춰졌습니다! **이제 2단계인 "임시 DB에서 1,000개씩 긁어와서 AI 배치 API 호출하기"로 넘어가 볼까요?** 이 단계에서는 AI API 특성에 맞춘 `ItemWriter` 작성이 핵심입니다.