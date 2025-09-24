# ----- Build Stage -----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

# ----- Run Stage -----
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port your app listens on (Render sets PORT env variable)
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
