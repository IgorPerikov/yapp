FROM openjdk:8u151-jre-alpine
COPY /build/libs/yapp-auth.jar .
EXPOSE 8083
CMD ["java", "-Xmx1g", "-Xms1g", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "yapp-auth.jar"]
