FROM gradle:8.12.1-jdk21 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean build --no-daemon --stacktrace

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar reposcore-app.jar
EXPOSE 8080
CMD ["java", "-jar", "reposcore-app.jar"]