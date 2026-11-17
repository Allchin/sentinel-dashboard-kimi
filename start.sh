#!/usr/bin/env bash
# Sentinel Dashboard（Redis 规则持久化版）多环境启动脚本
# 用法：
#   ./start.sh [dev|zrxdev|test|wms|export|prod]
#   SENTINEL_DASHBOARD_PORT=8870 ./start.sh zrxdev   # 自定义端口
# 各环境 Redis 配置见 src/main/resources/application-{env}.properties，
# 也可用 SENTINEL_RULE_REDIS_HOST/PORT/PASSWORD/DATABASE 环境变量覆盖。
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$DIR/target/sentinel-dashboard.jar"

ENV_NAME="${1:-dev}"
case "$ENV_NAME" in
  dev)     PROFILE="dev";     DEFAULT_PORT=8858 ;;
  zrxdev)  PROFILE="zrxdev";  DEFAULT_PORT=8859 ;;
  test)    PROFILE="test";    DEFAULT_PORT=8860 ;;
  wms)     PROFILE="wms";     DEFAULT_PORT=8861 ;;
  export)  PROFILE="export";  DEFAULT_PORT=8862 ;;
  prod)    PROFILE="prod";    DEFAULT_PORT=8863 ;;
  *)
    echo "未知环境: $ENV_NAME（支持: dev zrxdev test wms export prod）"
    exit 1
    ;;
esac

if [[ ! -f "$JAR" ]]; then
  echo "缺少 $JAR，请先构建：mvn clean package -DskipTests"
  exit 1
fi

PORT="${SENTINEL_DASHBOARD_PORT:-$DEFAULT_PORT}"

echo "Sentinel Dashboard [$ENV_NAME] 启动中..."
echo "  profile : $PROFILE"
echo "  port    : $PORT"
echo "  redis   : ${SENTINEL_RULE_REDIS_HOST:-默认值见 application-$PROFILE.properties}"
echo "  日志目录: ~/logs/csp/   停止: Ctrl+C"

# JDK 17/21 需要 add-opens（Spring Boot 2.0.5 / Tomcat 8.5）
exec java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/sun.net.util=ALL-UNNAMED \
  -Dserver.port="$PORT" \
  -Dcsp.sentinel.dashboard.server=127.0.0.1:"$PORT" \
  -Dproject.name=sentinel-dashboard \
  -Dsentinel.dashboard.auth.username="${SENTINEL_DASHBOARD_USER:-sentinel}" \
  -Dsentinel.dashboard.auth.password="${SENTINEL_DASHBOARD_PASS:-sentinel}" \
  -Dspring.profiles.active="$PROFILE" \
  -jar "$JAR"
