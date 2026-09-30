FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY ecogiro/ .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0"
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
