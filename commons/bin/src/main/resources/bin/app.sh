#!/usr/bin/env bash

APP_NAME="your_java_app"  # 应用名称，可自定义
JAR_FILE="your_app.jar"  # JAR 文件名称
JAVA_OPTS="-Xmx512m -Xms256m"  # Java 启动参数
PID_FILE="/var/run/${APP_NAME}.pid"  # PID 文件路径

start() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "$APP_NAME is already running (PID: $PID)."
            return 1
        else
            rm -f "$PID_FILE"
        fi
    fi
    echo "Starting $APP_NAME..."
    nohup java $JAVA_OPTS -jar $JAR_FILE > /dev/null 2>&1 &
    echo $! > "$PID_FILE"
    echo "$APP_NAME started (PID: $(cat $PID_FILE))."
}

stop() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "Stopping $APP_NAME (PID: $PID)..."
            kill $PID
            sleep 5
            if ps -p $PID > /dev/null 2>&1; then
                echo "Failed to stop $APP_NAME. Trying to kill forcefully..."
                kill -9 $PID
            fi
            rm -f "$PID_FILE"
            echo "$APP_NAME stopped."
        else
            echo "$APP_NAME is not running. Removing stale PID file."
            rm -f "$PID_FILE"
        fi
    else
        echo "$APP_NAME is not running."
    fi
}

status() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "$APP_NAME is running (PID: $PID)."
        else
            echo "$APP_NAME is not running. Removing stale PID file."
            rm -f "$PID_FILE"
        fi
    else
        echo "$APP_NAME is not running."
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {start|stop|status}"
        exit 1
        ;;
esac

exit 0
