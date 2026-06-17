FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/saccos-rules-service-1.0.0.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
