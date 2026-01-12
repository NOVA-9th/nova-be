🌟 NOVA

---

## 1. 프로젝트 소개

추후 추가 예정


  
팀장: 000 
팀원: 000, 000, 000


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
- 추후 사진 추가


### DevOps & Infra
- 


---

## 3. Git Convention

### 3-1. 브랜치 전략
Git Flow (변형 / Simplified Git Flow)

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



---

## 5. 기타
