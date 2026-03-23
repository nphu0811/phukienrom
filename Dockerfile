# ════════════════════════════════════════════
# Phukienrom - Multi-stage Docker Build
# Stage 1: Build with Gradle
# Stage 2: Run with JRE only (smaller image)
# WHY multi-stage: Final image is ~250MB vs 800MB+ with JDK
# ════════════════════════════════════════════

# ── Stage 1: Build ──────────────────────────
FROM gradle:8.14-jdk21-alpine AS builder
WORKDIR /app

# Cache dependencies first (layer cache optimization)
# WHY: Only re-download deps if build.gradle changes
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# Build the app
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ── Stage 2: Runtime ────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: don't run as root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
