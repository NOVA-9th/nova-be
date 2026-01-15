# ========== Build stage ==========
FROM gradle:8.8-jdk17 AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# ========== Runtime stage ==========
FROM eclipse-temurin:17-jre
WORKDIR /app

# healthcheck용 curl
RUN apt-get update && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]