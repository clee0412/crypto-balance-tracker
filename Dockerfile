#FROM eclipse-temurin:21.0.1_12-jdk
#RUN mkdir "/home/crypto-balance-tracker"
#WORKDIR .
#COPY /build/libs/crypto-balance-tracker.jar .
#EXPOSE 8080
#CMD ["java", "-jar", "crypto-balance-tracker.jar"]

# ---- Build stage ----
FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
