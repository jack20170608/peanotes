#!/bin/bash

set -e

JAR_FILE="/home/jack/apps/host-helper-server/host-helper-server-master.1-SNAPSHOT-jar-with-dependencies.jar"
JAVA_CMD="$JAVA_HOME/bin/java"

JVM_OPTS="-Xmx512m -Xms256m"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error, the JAR file $JAR_FILE not exists, please check!"
    exit 1
fi

echo "Starting the hosthelper ..."
$JAVA_CMD $JVM_OPTS -Denv=sit -jar $JAR_FILE &

PID=$!
echo "hosthelper started, the process ID is $PID。"

echo $PID > hosthelper.pid

echo "Start successfully!"
