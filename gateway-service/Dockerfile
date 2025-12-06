# -------- Stage 1: Build the application --------
FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace

COPY . /workspace

# Build the JAR using Gradle
RUN gradle clean bootJar --no-daemon

# -------- Stage 2: Run the application --------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8222

ENTRYPOINT ["java", "-jar", "app.jar"]
