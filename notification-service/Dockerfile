FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar notification-service.jar
COPY src/main/resources/application.yml ./application.yml

ENTRYPOINT ["java", "-Dspring.config.location=file:/app/application.yml", "-jar", "notification-service.jar"]