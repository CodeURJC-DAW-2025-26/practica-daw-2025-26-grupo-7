# Stage 1: Build React frontend
FROM node:22-alpine AS frontend-build

WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot JAR with frontend included
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build

WORKDIR /project

COPY backend/pom.xml /project/pom.xml
COPY backend/src /project/src

# Copy the React build into Spring Boot static resources so it's bundled in the JAR
COPY --from=frontend-build /frontend/build/client /project/src/main/resources/static/new

RUN mvn -f /project/pom.xml clean package -DskipTests

# Stage 3: Minimal runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-build /project/target/*.jar /app/app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
