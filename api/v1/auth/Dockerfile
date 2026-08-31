FROM maven:3.9-eclipse-temurin-26-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B -q

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -B -q

FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]