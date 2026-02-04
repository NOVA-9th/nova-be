package com.nova.nova_server.domain.batch.converter;

import com.nova.nova_server.domain.post.model.Article;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Article → LLM 요약용 프롬프트 문자열 변환
 * 요약, 근거, 키워드 5개 추출을 위한 프롬프트 생성
 */
@Component
public class ArticleToPromptConverter {

    private static final String SUMMARY_PROMPT = """
            다음 기사 내용을 바탕으로 JSON 형태로 요약(summary), 근거(evidence), 키워드 5개(keywords)를 추출해주세요.
            반드시 아래 형식으로만 응답하세요: {"summary":"...", "evidence":"...", "keywords":["...","...","...","...","..."]}

            [중요 지침]
            1. keywords는 반드시 아래 제공된 '허용된 키워드 목록'에서만 정확히 선택해야 합니다.
            2. 기사의 내용과 가장 관련이 깊은 키워드 5개를 선택하세요.
            3. 목록에 없는 키워드는 절대로 사용하지 마세요.

            [허용된 키워드 목록]
            - Mobile App: Android, iOS, Flutter, React Native, Kotlin, Swift, SwiftUI, Jetpack Compose, Firebase, Mobile CI/CD
            - Web: HTML, CSS, JavaScript, TypeScript, React, Next.js, Vue.js, Nuxt, Svelte, Vite
            - Backend: Java, Spring, Spring Boot, Node.js, Express, NestJS, REST API, GraphQL, JPA, MySQL
            - Full-stack: Full Stack, MERN Stack, MEAN Stack, Next.js Fullstack, BFF (Backend for Frontend), Prisma, Supabase, Firebase Auth, API Design, Monorepo
            - AI/ML: Machine Learning, Deep Learning, LLM, Prompt Engineering, Fine-tuning, RAG (Retrieval Augmented Generation), OpenAI API, LangChain, Vector Database, AI Ethics
            - Data Engineering: ETL, Data Pipeline, Apache Airflow, Apache Spark, Kafka, Hadoop, Data Warehouse, BigQuery, Snowflake, Batch Processing
            - Data Analysis: Python, Pandas, NumPy, SQL, Data Visualization, Tableau, Power BI, A/B Testing, Statistics, Exploratory Data Analysis
            - Infra: AWS, GCP, Azure, Docker, Kubernetes, CI/CD, Terraform, Nginx, Load Balancer, Cloud Architecture
            - Security: Cyber Security, OWASP, Web Security, Penetration Testing, Encryption, OAuth, JWT, Zero Trust, Security Audit, Vulnerability Analysis
            - Networking: TCP/IP, HTTP/HTTPS, DNS, CDN, Load Balancing, Network Protocols, Network Security, VPN, Routing, Switching
            - Embedded Systems: Embedded C, Microcontroller, ARM, RTOS, Firmware, IoT, Raspberry Pi, Arduino, Device Driver, Embedded Linux
            - Blockchain: Blockchain, Smart Contract, Ethereum, Solidity, Web3, DeFi, NFT, Layer2, Consensus Algorithm, Cryptography
            - Computer Vision: Computer Vision, Image Processing, OpenCV, CNN, Object Detection, Image Segmentation, OCR, Face Recognition, YOLO, Vision Transformer
            - Game Dev: Game Engine, Unity, Unreal Engine, C# Game Dev, Game Physics, Multiplayer Server, Game AI, Rendering, Shader, Game Optimization
            - System Engineering: Operating System, Linux, Kernel, System Programming, Process Management, Memory Management, File System, Shell Script, Performance Tuning, Low-level Programming
            - QA: Software Testing, Test Automation, Selenium, Cypress, Unit Test, Integration Test, E2E Test, Test Case Design, QA Process, Bug Tracking

            기사 내용:
            """;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Article 목록을 LLM 배치 입력용 Map으로 변환
     *
     * @param articles 아티클 목록
     * @return custom_id(key) -> 프롬프트+아티클 조합 문자열(value)의 Map
     */
    public java.util.Map<String, String> toPromptMap(List<Article> articles) {
        java.util.Map<String, String> resultMap = new java.util.HashMap<>();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            String customId = "article-" + i;
            String promptContent = SUMMARY_PROMPT + formatArticle(article);
            resultMap.put(customId, promptContent);
        }
        return resultMap;
    }

    private String formatArticle(Article article) {
        StringBuilder sb = new StringBuilder();
        sb.append("제목: ").append(nullToEmpty(article.title())).append("\n");
        sb.append("출처: ").append(nullToEmpty(article.source())).append("\n");
        sb.append("작성자: ").append(nullToEmpty(article.author())).append("\n");
        if (article.publishedAt() != null) {
            sb.append("발행일: ").append(article.publishedAt().format(FORMATTER)).append("\n");
        }
        sb.append("본문: ").append(nullToEmpty(article.content()));
        return sb.toString();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
