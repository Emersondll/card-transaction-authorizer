# ============================================================
# Dockerfile — Multi-Stage Build (Spec 06-DOCKER-DEPLOYMENT)
# Stage 1: Build  |  Stage 2: Runtime (minimal JRE image)
# ============================================================

# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-22 AS builder

LABEL maintainer="Emerson Lima <https://github.com/Emersondll>"

WORKDIR /app

# Cache dependency layer separately from source code
COPY pom.xml .
COPY lombok.config .
RUN mvn dependency:resolve-plugins dependency:resolve -q

COPY src ./src

RUN mvn clean package -DskipTests -q \
    && ls -la target/*.jar

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:22-jre-jammy

LABEL maintainer="Emerson Lima <https://github.com/Emersondll>"
LABEL version="1.0.0"
LABEL description="Card Transaction Authorizer — Java 22 + Spring Boot 3"

WORKDIR /app

# Create non-root user for security (Spec 06)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy JAR from builder stage
COPY --from=builder /app/target/transactionauthorizer-*.jar application.jar

RUN chown -R appuser:appuser /app

USER appuser

# Health check via Spring Boot Actuator (Spec 05/06)
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# JVM tuning for containers (Spec 06)
ENV JVM_OPTS="\
    -Xms256m \
    -Xmx512m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseContainerSupport \
    -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar application.jar"]
