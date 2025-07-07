# Multi-stage Dockerfile for coursemgmt-service
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only dependency files first for better layer caching
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER  spring:spring
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
