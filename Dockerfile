FROM maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

# run stage
FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
