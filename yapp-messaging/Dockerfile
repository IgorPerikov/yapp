FROM openjdk:8u151-jdk-alpine AS BUILD_IMAGE
WORKDIR /
COPY . /
RUN ./../gradlew build

FROM openjdk:8u151-jre-alpine
COPY --from=BUILD_IMAGE /build/libs/yapp-messaging.jar .
EXPOSE 8081
CMD ["java", "-Xmx1g", "-Xms1g", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "yapp-messaging.jar"]
