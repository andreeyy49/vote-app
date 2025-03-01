FROM openjdk:17-jdk-slim

WORKDIR /voting-service

COPY target/voting-service-0.0.1-SNAPSHOT.jar voting-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "voting-service.jar"]