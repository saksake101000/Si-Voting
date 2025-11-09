# Multi-stage Dockerfile for serverapp (Spring Boot)
# Builds the application using Maven and produces a runnable thin JAR

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn ./
# Copy project sources
COPY src ./src

# Use Maven to build the jar
RUN mvn -B -DskipTests package

# Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy jar from build stage
COPY --from=build /workspace/target/*.jar app.jar

# Optional: create a non-root user
RUN addgroup -S app && adduser -S app -G app
USER app

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
