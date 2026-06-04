FROM eclipse-temurin:21-jre
WORKDIR /app
COPY service/build/libs/*.jar app.jar
EXPOSE 8080 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
