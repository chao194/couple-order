# 用 Java 17 官方镜像
FROM openjdk:17-jdk-slim

# 把你打包好的 JAR 文件复制到容器里
COPY target/*.jar app.jar

# 启动命令
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 暴露 Spring Boot 默认端口
EXPOSE 8080
