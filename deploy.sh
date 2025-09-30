#!/bin/bash

PROJECT_DIR="/Users/matt/IdeaProjects/student-affair-service"
JAR_PATH="$PROJECT_DIR/target/student-affair-service-1.0-SNAPSHOT.jar"
SERVER="root@47.120.37.33"
REMOTE_DIR="/home/student-affair"
CONTAINER_NAME="student-affair-server-5006"
IMAGE_NAME="student-affair-server"

echo ">>> 上传 JAR 包..."
scp $JAR_PATH $SERVER:$REMOTE_DIR/

echo ">>> 登录服务器部署 Docker..."
ssh $SERVER "
  docker rm -f $CONTAINER_NAME 2>/dev/null || true &&
  docker build -t $IMAGE_NAME $REMOTE_DIR &&
  docker run -d \
    --name $CONTAINER_NAME \
    --ulimit nofile=65535:65535 \
    --network host \
    --restart unless-stopped \
    $IMAGE_NAME \
    --spring.profiles.active=cloud &&
  docker logs -f --tail 100 $CONTAINER_NAME
"