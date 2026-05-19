# 最新可用 Java 17 镜像（100%能跑）
FROM eclipse-temurin:17-jre-alpine

# 工作目录
WORKDIR /app

# 复制打包好的 jar 包
COPY target/*.jar app.jar

# 启动项目
ENTRYPOINT ["java", "-jar", "app.jar"]

# 暴露端口
EXPOSE 8080