FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8222

ENTRYPOINT ["java", "-jar", "app.jar"]
