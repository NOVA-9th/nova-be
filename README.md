🌟 NOVA

---

## 1. 프로젝트 소개

NOVA는 IT 분야 취업 준비생을 위한 개인화 콘텐츠 큐레이션 서비스입니다. AI 기반 아티클 요약, 키워드 트렌드 분석, 맞춤형 피드를 통해 빠르게 변화하는 IT 업계 동향을 효율적으로 파악할 수 있도록 돕기 위한 프로젝트 입니다.


  
팀장: 백준규
팀원: 권세인, 나강건, 장은지, 조성준


---

## 2. 기술 스택

### Java Spring
- project build : Gradle 
- Spring Boot : 3.5.9  
- Java : 17
- packaging : jar
- IDE : Intellij  
- 패키지 전략: 도메인 패키지 전략

### 데이터베이스
- MySQL

### 라이브러리
- Spring Web
- Spring Data JPA
- MySQL Driver
- Lombok


  
### 서버 아키텍처 다이어그램
<img width="787" height="619" alt="image" src="https://github.com/user-attachments/assets/d05e81a7-958b-4dbc-91f6-433bb6a03162" />


### 모니터링
- Grafana (데이터 시각화 및 대시보드)
- Prometheus (어플리케이션 메트릭 수집)
- Loki (로그 중앙 집중 관리)
- Spring Boot Actuator (어플리케이션 상태 및 엔드포인트 모니터링)



---

## 3. Git Convention

### 3-1. 브랜치 전략
Gitub Flow

- **main** : 배포 버전
- **develop** : 개발 통합 브랜치
- **feature/** : 기능 개발 (예: `feature/reservation-api`)


- 브랜치명은 **kebab-case** 사용
- 모든 작업은 **이슈 생성 후 브랜치 생성**
- 기본 merge 대상 브랜치는 `develop`

---

### 3-2. 이슈(Issue) 규칙

| 항목 | 규칙 | 예시 |
|----|----|----|
| 제목 형식 | `[TYPE] 작업 요약` | `[CHORE] 프로젝트 초기 세팅` |
| TYPE | 대문자 사용 | `CHORE`, `FEAT`, `FIX` |
| 목적 | 작업 단위 관리 | 로그인 API 구현 |
| 브랜치 연결 | 이슈 번호 기반 | `feat/#12-login-api` |

---

### 3-3. 커밋 메시지 규칙

| 항목 | 규칙 | 예시 |
|----|----|----|
| 기본 형식 | `[TYPE] 작업 내용` | `[FEAT] 로그인 API 구현` |
| TYPE | 이슈 TYPE과 동일 | `CHORE`, `FEAT`, `FIX` |
| 언어 | 한글 허용 | 설정 파일 추가 |
| 단위 | 의미 단위 커밋 | 기능 / 설정 분리 |

---

### 3-4. Pull Request(PR) 규칙

| 항목 | 규칙 | 예시 |
|----|----|----|
| 제목 형식 | `[TYPE/#이슈번호] 작업 요약` | `[FEAT/#12] 로그인 API 구현` |
| 대상 브랜치 | `develop` | develop ← feature |
| 이슈 연결 | `closes #이슈번호` | `closes #12` |
| merge 방식 | PR 기반 merge | 리뷰 후 merge |

---

### 3-5. 전체 작업 흐름 예시

| 단계 | 예시 |
|----|----|
| 이슈 | `[FEAT] 로그인 API 구현` |
| 브랜치 | `feat/#12-login-api` |
| 커밋 | `[FEAT] 로그인 API 구현` |
| PR | `[FEAT/#12] 로그인 API 구현` |
| merge | `develop` 브랜치 |

---

## 4. 프로젝트 구조

nova-server
├── docs/                 # 프로젝트 관련 문서 (API 설계, 인프라 등)
├── scripts/               # 배포 및 실행 관리 스크립트
├── src/
│   ├── main/
│   │   ├── java/com/nova/nova_server/
│   │   │   ├── domain/                              # 비즈니스 로직 (도메인별 분리)
│   │   │   │   ├── ai/                              # AI 요약 및 분석 기능
│   │   │   │   ├── auth/                            # 인증/인가 (JWT, Security, OAuth2)
│   │   │   │   ├── batch/                           # 배치 작업 및 스케줄러 (Spring Batch)
│   │   │   │   │   ├── articleingestion/            # 기사 수집 배치 프로세스
│   │   │   │   │   ├── cardnews/                    # 카드뉴스 생성 배치 프로세스
│   │   │   │   │   ├── summary/                     # 기사 요약 배치 프로세스
│   │   │   │   │   └── common/                      # 배치 공통 컴포넌트 및 메타데이터 관리
│   │   │   │   ├── bookmark/                        # 북마크/스크랩 기능
│   │   │   │   ├── cardNews/                        # 카드뉴스 관련 API 및 도메인
│   │   │   │   ├── feed/                            # 통합 피드 목록 조회 및 관리
│   │   │   │   ├── keyword/                         # 키워드 추출 및 트렌드 데이터
│   │   │   │   ├── member/                          # 사용자 정보 및 프로필 관리
│   │   │   │   ├── post/                            # 게시글 및 외부 기사 데이터 관리
│   │   │   │   │   ├── sources/                     # 외부 API 소스별 구현체 (기술블로그, GNews, Jumpit 등)
│   │   │   │   │   ├── model/                       # 포스트 관련 공통 모델 정의
│   │   │   │   │   └── service/                     # 포스트 처리 비즈니스 로직
│   │   │   │   └── trend/                           # 실시간 인기 키워드 및 트렌드 API
│   │   │   └── global/                              # 전역 설정 및 공통 처리
│   │   │       ├── apiPayload/                      # 통합 응답 규격 및 예외 처리기
│   │   │       └── config/                          # 프레임워크 설정 (Security, Swagger, WebClient 등)
│   │   └── resources/                               # 애플리케이션 설정 및 로그 설정 (Logback)
│   └── test/                                        # 단위 및 통합 테스트 코드
└── build.gradle                                     # 프로젝트 의존성 및 빌드 설정


---

## 5. 기타 (프로젝트 랜딩 페이지)

<img width="7680" height="4320" alt="nova 온보딩1" src="https://github.com/user-attachments/assets/a5147e06-062f-4377-9f39-bf78d52fc7fb" />

<img width="7680" height="4320" alt="nova 온보딩2" src="https://github.com/user-attachments/assets/ec98b6d9-3d43-4740-b759-088cb48b93ce" />

<img width="7680" height="4320" alt="nova 온보딩3" src="https://github.com/user-attachments/assets/3624d08d-ccf1-4b2c-961c-abf1817ab2a4" />

<img width="7680" height="4320" alt="nova 온보딩4" src="https://github.com/user-attachments/assets/1cc8880d-d076-499b-9c34-97a1bc219790" />

<img width="7680" height="4320" alt="nova 온보딩5" src="https://github.com/user-attachments/assets/5dc17a15-d838-4dc4-a22b-349cfd097c0e" />

<img width="7680" height="4320" alt="nova 온보딩6" src="https://github.com/user-attachments/assets/9ede2d34-f0ce-4e45-82d3-d7271aa274e8" />

<img width="7680" height="4320" alt="nova 온보딩7" src="https://github.com/user-attachments/assets/e9bd0b87-1520-4515-9f69-6ce87068d7b3" />


