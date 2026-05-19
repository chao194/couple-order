FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY . .

RUN javac -cp "src/main/java" src/main/java/com/example/coupleorder/CoupleOrderApplication.java

ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080