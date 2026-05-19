# 第一阶段：用 Maven 构建
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# 第二阶段：运行 JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/couple-order-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
