FROM bellsoft/liberica-openjdk-debian:27 AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw -B -ntp -DskipTests package

FROM bellsoft/liberica-openjre-debian:27
WORKDIR /app
COPY --from=build /app/target/tinylink-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
