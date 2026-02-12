# 🌟 NOVA

<img width="7680" height="4320" alt="nova 온보딩1" src="https://github.com/user-attachments/assets/3c43ed1a-aaf7-43dd-b539-4d720947f672" />


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
├── docs/                         # 프로젝트 문서 (API 설계, 인프라 아키텍처, 운영 전략)
├── scripts/                      # 배포 및 실행 자동화 스크립트
├── src/
│   ├── main/
│   │   ├── java/com/nova/nova_server/
│   │   │
│   │   │   ├── domain/           # 핵심 비즈니스 도메인 레이어
│   │   │   │
│   │   │   │   ├── ai/           # AI 요약 및 분석 로직
│   │   │   │   ├── auth/         # 인증/인가 (JWT, Spring Security, OAuth2)
│   │   │   │   ├── batch/        # Spring Batch 기반 데이터 처리
│   │   │   │   │   ├── articleingestion/   # 기사 수집 배치
│   │   │   │   │   ├── summary/            # 기사 요약 배치
│   │   │   │   │   ├── cardnews/           # 카드뉴스 생성 배치
│   │   │   │   │   └── common/             # 배치 공통 설정 및 메타데이터
│   │   │   │   ├── bookmark/     # 북마크(스크랩) 기능
│   │   │   │   ├── cardNews/     # 카드뉴스 API 및 도메인
│   │   │   │   ├── feed/         # 통합 피드 조회 및 정렬
│   │   │   │   ├── keyword/      # 키워드 추출 및 트렌드 분석
│   │   │   │   ├── member/       # 사용자 프로필 및 계정 관리
│   │   │   │   ├── post/         # 기사 및 외부 콘텐츠 관리
│   │   │   │   │   ├── sources/  # 외부 API 구현체 (Tech Blog, GNews, Jumpit 등)
│   │   │   │   │   ├── model/    # Post 공통 모델 정의
│   │   │   │   │   └── service/  # 콘텐츠 처리 비즈니스 로직
│   │   │   │   └── trend/        # 실시간 인기 트렌드 API
│   │   │   │
│   │   │   └── global/           # 전역 공통 모듈
│   │   │       ├── apiPayload/   # 공통 응답 포맷 및 예외 처리
│   │   │       └── config/       # 프레임워크 설정 (Security, Swagger, WebClient 등)
│   │   │
│   │   └── resources/           
│   │
│   └── test/                     #  단위 및 통합 테스트
│
└── build.gradle                  #  프로젝트 의존성 및 빌드 설정


---

## 5. 기타 (프로젝트 랜딩 페이지)

<img width="7680" height="4320" alt="nova 온보딩1" src="https://github.com/user-attachments/assets/a5147e06-062f-4377-9f39-bf78d52fc7fb" />

<img width="7680" height="4320" alt="nova 온보딩2" src="https://github.com/user-attachments/assets/ec98b6d9-3d43-4740-b759-088cb48b93ce" />

<img width="7680" height="4320" alt="nova 온보딩3" src="https://github.com/user-attachments/assets/3624d08d-ccf1-4b2c-961c-abf1817ab2a4" />

<img width="7680" height="4320" alt="nova 온보딩4" src="https://github.com/user-attachments/assets/1cc8880d-d076-499b-9c34-97a1bc219790" />

<img width="7680" height="4320" alt="nova 온보딩5" src="https://github.com/user-attachments/assets/5dc17a15-d838-4dc4-a22b-349cfd097c0e" />

<img width="7680" height="4320" alt="nova 온보딩6" src="https://github.com/user-attachments/assets/9ede2d34-f0ce-4e45-82d3-d7271aa274e8" />

<img width="7680" height="4320" alt="nova 온보딩7" src="https://github.com/user-attachments/assets/e9bd0b87-1520-4515-9f69-6ce87068d7b3" />


