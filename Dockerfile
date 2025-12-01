# builder stage: build fat jar using maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
# cache dependencies
RUN mvn -q -B dependency:go-offline

COPY src ./src
# build jar (adjust -DskipTests if you want)
RUN mvn -q -B package -DskipTests

# runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# copy jar from builder (assumes spring-boot-starter creates jar in target/)
COPY --from=build /workspace/target/*-SNAPSHOT.jar app.jar
# if your jar has different name, adjust accordingly

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
