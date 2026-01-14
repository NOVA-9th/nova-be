/* 1. 독립적인 기초 테이블 (먼저 생성) */

-- 관심사 (Interest)
CREATE TABLE `interest` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `name`          VARCHAR(50)     NOT NULL COMMENT '관심사 명 (예: 경제, IT)',
    PRIMARY KEY (`id`)
);

-- 카드 뉴스 타입 (Card Type)
CREATE TABLE `card_type` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `name`          VARCHAR(50)     NOT NULL COMMENT '타입 명 (예: 뉴스, 팁)',
    PRIMARY KEY (`id`)
);

/* 2. 주요 엔티티 테이블 */

-- 키워드 (Keyword)
-- 기존 복합키(Key, Key2) 구조를 단일 PK로 변경하고 Interest를 외래키로 가짐
CREATE TABLE `keyword` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `interest_id`   BIGINT          NOT NULL COMMENT 'FK: 관심사 ID',
    `name`          VARCHAR(100)    NOT NULL COMMENT '키워드 명',
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_interest_TO_keyword` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`) ON DELETE CASCADE
);

-- 카드 뉴스 (Card News)
CREATE TABLE `card_news` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `card_type_id`      BIGINT          NOT NULL COMMENT 'FK: 카드 타입 (기존 Key3)',
    `title`             VARCHAR(255)    NOT NULL COMMENT '아티클 제목',
    `author`            VARCHAR(100)    NULL     COMMENT '작성자',
    `published_at`      DATETIME        NULL     COMMENT '작성일자 (VARCHAR -> DATETIME)',
    `summary`           TEXT            NULL     COMMENT '요약 (긴 내용 대응 TEXT)',
    `evidence`          VARCHAR(255)    NULL     COMMENT '근거',
    `original_url`      VARCHAR(2048)   NULL     COMMENT '원문 URL',
    `source_site_name`  VARCHAR(100)    NULL     COMMENT '출처 사이트 이름',
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_card_type_TO_card_news` FOREIGN KEY (`card_type_id`) REFERENCES `card_type` (`id`)
);

-- 회원 (Member)
CREATE TABLE `member` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `email`         VARCHAR(255)    NOT NULL UNIQUE COMMENT '이메일',
    `name`          VARCHAR(100)    NOT NULL COMMENT '이름',
    `level`         ENUM('BASIC', 'VIP', 'ADMIN') DEFAULT 'BASIC' COMMENT '등급',
    `background`    VARCHAR(255)    NULL COMMENT '소속/배경',
    `profile_image` LONGBLOB        NULL COMMENT '프로필 이미지 (Field4)',
    `phone`         VARCHAR(20)     NULL COMMENT '연락처 (Field3)',
    `sns_id`        VARCHAR(255)    NULL COMMENT 'SNS 연동 ID (Field5)',
    `extra_info`    VARCHAR(255)    NULL COMMENT '기타 정보 (Field6)',
    `created_at`    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    PRIMARY KEY (`id`)
);

/* 3. 통계 및 관계 매핑 테이블 */

-- 키워드 통계 (Keyword Statistics)
CREATE TABLE `keyword_statistics` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `keyword_id`    BIGINT          NOT NULL COMMENT 'FK: 키워드 ID',
    `stat_date`     DATE            NOT NULL COMMENT '날짜',
    `mention_count` INT             DEFAULT 0 COMMENT '언급 수 (VARCHAR -> INT)',
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_keyword_TO_stats` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`id`) ON DELETE CASCADE
);

-- 카드뉴스 - 키워드 연결 (Card News Keyword)
CREATE TABLE `card_news_keyword` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `card_news_id`  BIGINT          NOT NULL,
    `keyword_id`    BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_cnk_card_news_keyword` (`card_news_id`, `keyword_id`),
    CONSTRAINT `FK_card_news_TO_cnk` FOREIGN KEY (`card_news_id`) REFERENCES `card_news` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_keyword_TO_cnk` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`id`) ON DELETE CASCADE
);

-- 회원 선호 관심사 (Member Prefer Interest)
CREATE TABLE `member_prefer_interest` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `member_id`     BIGINT          NOT NULL,
    `interest_id`   BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_mpi_member_interest` (`member_id`, `interest_id`),
    CONSTRAINT `FK_member_TO_mpi` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_interest_TO_mpi` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`) ON DELETE CASCADE
);

-- 회원 선호 키워드 (Member Prefer Keyword)
CREATE TABLE `member_prefer_keyword` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `member_id`     BIGINT          NOT NULL,
    `keyword_id`    BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_mpk_member_keyword` (`member_id`, `keyword_id`),
    CONSTRAINT `FK_member_TO_mpk` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_keyword_TO_mpk` FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`id`) ON DELETE CASCADE
);

/* 4. 사용자 활동 데이터 (Relevance, Bookmark) */

-- 카드뉴스 관련도 점수 (Card News Relevance)
-- (기존 Untitled5)
CREATE TABLE `card_news_relevance` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `member_id`     BIGINT          NOT NULL,
    `card_news_id`  BIGINT          NOT NULL,
    `score`         DOUBLE          DEFAULT 0.0 COMMENT '관련도 점수',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_relevance_member_card_news` (`member_id`, `card_news_id`),
    CONSTRAINT `FK_member_TO_relevance` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_card_news_TO_relevance` FOREIGN KEY (`card_news_id`) REFERENCES `card_news` (`id`) ON DELETE CASCADE
);

-- 카드뉴스 북마크 (Card News Bookmark)
-- (기존 Untitled6)
CREATE TABLE `card_news_bookmark` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `member_id`     BIGINT          NOT NULL,
    `card_news_id`  BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_bookmark_member_card_news` (`member_id`, `card_news_id`),
    CONSTRAINT `FK_member_TO_bookmark` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_card_news_TO_bookmark` FOREIGN KEY (`card_news_id`) REFERENCES `card_news` (`id`) ON DELETE CASCADE
);