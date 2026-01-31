# Build stage: use Gradle to build the fat JAR
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and build config first (better layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source and build the boot JAR
COPY src src
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR from build stage (Gradle puts it in build/libs/)
COPY --from=build /app/build/libs/saltyserver-*.jar /app/saltyserver.jar

# Data dir for H2 DB and uploaded images (override paths via env; see docker-compose)
RUN mkdir -p /app/data && chown -R spring:spring /app

USER spring:spring
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app/saltyserver.jar"]
