# Multi-stage Dockerfile for clientapp (Spring Boot)
# Builds the application using Maven and runs the Spring Boot app separately (port 8081)

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn ./
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

RUN addgroup -S app && adduser -S app -G app
USER app

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8081} -jar /app/app.jar"]
