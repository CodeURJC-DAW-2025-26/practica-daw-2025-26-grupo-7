FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /project

COPY backend/pom.xml /project/pom.xml
COPY backend/src /project/src

RUN mvn -f /project/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /project/target/*.jar /app/app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/app/app.jar"]