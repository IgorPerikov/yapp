FROM openjdk:8u151-jre-alpine
COPY /build/libs/yapp-integration.jar .
EXPOSE 8082
CMD ["java", "-Xmx700m", "-Xms700m", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "yapp-integration.jar"]
