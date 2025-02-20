FROM openjdk:17-jdk-slim

WORKDIR /auth-service

COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "auth-service.jar"]