# 使用官方 OpenJDK 17 slim 镜像作为基础
FROM openjdk:17-jdk-slim

# 安装 EasyExcel 运行所需的字体库依赖
# libfreetype6: 提供字体渲染功能
# fontconfig: 用于管理和配置系统字体
# 这是解决 'libfontmanager.so' 错误的关键步骤
RUN apt-get update && \
    apt-get install -y libfreetype6 fontconfig && \
    rm -rf /var/lib/apt/lists/*

# 设置时区为上海和系统编码为UTF-8
ENV LANG=C.UTF-8
ENV TZ=Asia/Shanghai

# 设置工作目录
WORKDIR /app

# 复制 Maven 打包后的 jar 文件到工作目录中
COPY student-affair-service-1.0-SNAPSHOT.jar app.jar

# 对外暴露 Spring Boot 默认端口
EXPOSE 5006

# 启动程序，并指定使用 cloud 配置文件
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=cloud"]
