# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
# Download dependencies first (layer cache)
RUN mvn -B dependency:go-offline -q

COPY src ./src
RUN mvn -B package -DskipTests -q

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/servicedesk-lite-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
