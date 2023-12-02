FROM openjdk:18-jdk-slim AS build

WORKDIR /app
COPY pom.xml /app/pom.xml
COPY mvnw /app/mvnw
COPY .mvn /app/.mvn

RUN ./mvnw verify clean --fail-never
COPY . /app
RUN ./mvnw clean package -DskipTests

FROM openjdk:18-jdk-slim AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar /app/ockr-api.jar
ENTRYPOINT ["java", "-jar", "ockr-api.jar"]