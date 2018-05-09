FROM openjdk:8u151-jre-alpine
COPY /build/libs/yapp-gateway.jar .
EXPOSE 8080
CMD ["java", "-Xmx1g", "-Xms1g", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "yapp-gateway.jar"]
