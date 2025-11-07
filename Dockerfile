# Stage 1: Build stage
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar provider-service.jar

EXPOSE 7073
ENTRYPOINT ["java", "-jar", "provider-service.jar"]
