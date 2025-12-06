# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew ./

# Copy source code
COPY src src

# Build application with memory limits
RUN ./gradlew clean build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m" \
    --max-workers=1

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install required libraries for Spring AI Transformers
RUN apk add --no-cache libstdc++

# Copy jar from build stage
COPY --from=build /app/build/libs/spring-ai-rag-agent-1.0.0.jar app.jar

# Expose port (Render will set PORT env variable)
EXPOSE 8080

# Run application
# Render sets PORT env variable dynamically
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
