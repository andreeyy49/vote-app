FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/geo-storage-service-0.0.1-SNAPSHOT.jar geo-storage-service.jar
COPY src/main/resources/application.yml ./application.yml

ENTRYPOINT ["java", "-Dspring.config.location=file:/app/application.yml", "-jar", "geo-storage-service.jar"]