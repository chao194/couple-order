# 1. 构建阶段：Maven 打包项目
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# 复制Maven配置和源码
COPY pom.xml .
COPY src ./src

# 打包生成JAR
RUN ./mvnw clean package -DskipTests

# 2. 运行阶段：最小镜像
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 从构建阶段复制JAR包
COPY --from=build /app/target/*.jar app.jar

# 启动
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080