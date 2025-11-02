#!/usr/bin/env bash

__SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
__SCRIPT_NAME=$(basename "$0")
__SCRIPT_VERSION="1.0.0"
__SCRIPT_AUTHOR="jack"

source "${__SCRIPT_DIR}"/log.bash
# shellcheck disable=SC2155
export APP_ROOT=$(cd "${__SCRIPT_DIR}/../" && pwd -P)
source "${__SCRIPT_DIR}"/common.bash

# The help function
function help() {
	echo "Name: $__SCRIPT_NAME"
	echo "Version: $__SCRIPT_VERSION"
	echo "Author: $__SCRIPT_AUTHOR"
	echo "Usage: $__SCRIPT_NAME [start|stop|restart|status|info|help]"
	echo "Options:"
	echo "  start      Start the app"
	echo "  stop       Stop the app"
	echo "  restart    Restart the app"
	echo "  status     Get the app status"
	echo "  info       Print the app info"
	echo "  help      Display this help message"
	echo ""
	echo "Example:"
	echo "  $__SCRIPT_NAME start"
	echo "  $__SCRIPT_NAME stop"
	echo "  $__SCRIPT_NAME restart"
	echo "  $__SCRIPT_NAME status"
	echo "  $__SCRIPT_NAME info"
	echo "  $__SCRIPT_NAME help"
}

log_info "Starting with APP_ROOT=$APP_ROOT"
log_line ""
ENV_NAME=$(cat "$APP_ROOT"/config/environment.cfg)
log_info "Environment name: $ENV_NAME"

if [ -f "$APP_ROOT"/config/setenv-common ]; then
	# shellcheck disable=SC1090
	source "$APP_ROOT"/config/setenv-common
fi

if [ -f "$APP_ROOT"/config/setenv-"${ENV_NAME}" ]; then
	# shellcheck disable=SC1090
	source "$APP_ROOT"/config/setenv-"${ENV_NAME}"
fi

##check and print mandatory env vars
check_mandatory_env_vars

print_mandatory_env_vars

###################################
#(函数)判断程序是否已启动
#
#说明：
#使用JDK自带的JPS命令及grep命令组合，准确查找pid
#jps 加 lv 参数，表示显示java的完整包路径
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
#初始化psid变量（全局）
psid=0

checkpid() {
	javaps=$($JAVA_HOME/bin/jps -v | grep $PROC_NAME | grep -v grep)

	if [ -n "$javaps" ]; then
		psid=$(echo $javaps | awk '{print $1}')
	else
		psid=0
	fi
}

###################################
#(函数)停止程序
###################################
kill_and_clean_process() {
	checkpid
	if [ $psid -ne 0 ]; then
		log_warn "Killing process with pid $psid..."
		kill $psid
		rm -f "$STATE_FILE"
		rm -f "$PID_FILE"
	fi
}

###################################
#(函数)强制停止程序
###################################
force_kill_and_clean_process() {
	checkpid
	if [ $psid -ne 0 ]; then
		log_warn "Killing process with pid $psid..."
		kill -9 $psid
		rm -f "$STATE_FILE"
		rm -f "$PID_FILE"
	fi
}

###################################
#(函数)启动程序
###################################
start() {
	checkpid

	if [ $psid -ne 0 ]; then
		log_info "================================"
		log_warn "$APP_NAME with process name $PROC_NAME already started! (pid=$psid)"
		exit 1
		log_info "================================"
	else
		log_info "Starting $APP_NAME with process name $PROC_NAME..."
		## apply the jvm args
		JVM_ARGS=
		for ((i = 1; i < 200; i++)); do
			cur_var="JVM_ARG_$i"
			if [ -n "${!cur_var}" ]; then
				JVM_ARGS="$JVM_ARGS ${!cur_var}"
			fi
		done
		CONSOLE_LOG="$LOG_HOME/$PROC_NAME-console.log"

		start_command_token=(
			"nohup $JAVA_HOME/bin/java"
			"-Dp.app.name=$APP_NAME"
			"-Dp.proc.name=$PROC_NAME"
			"-Dp.log.home=$LOG_HOME"
			"-Dp.pid.file=$PID_FILE"
			"-Dp.state.file=$STATE_FILE"
			"$JVM_ARGS"
			"> $CONSOLE_LOG 2>&1 &"
		)
		start_command=$(printf "%s " "${start_command_token[@]}")
		log_info "start command: $start_command"
		eval "$start_command"

		_start_wait_time=0
		# shellcheck disable=SC2046
		until [ $(cat "$STATE_FILE") = "RUNNING" ] || [ $(cat "$STATE_FILE") = "START_FAILED" ] || [ $_start_wait_time -gt "$STARTUP_TIMEOUT" ]; do
			sleep 5
			log_info "Waiting for $APP_NAME to start..."
			_start_wait_time=$((_start_wait_time + 5))
		done
		_start_result=$(cat "$STATE_FILE")

		if [ -n "$_start_result" ]; then
			log_info "Start result: $_start_result"
			if [ "$_start_result" = "RUNNING" ]; then
				log_info "SUCCESS: $APP_NAME is up and running with pid $psid"
				mv "$CONSOLE_LOG" "$LOG_HOME/$PROC_NAME-console.log.$(date '+%F %T')"
				exit 0
			elif [ "$_start_result" = "START_FAILED" ]; then
				log_error "FAILED: $APP_NAME failed to start up (status: $_start_result)"
				kill_and_clean_process
				exit 11
			else
				log_error "FAILED: $APP_NAME failed to start up (status: $_start_result)"
				kill_and_clean_process
				exit 12
			fi
		elif [ $_start_wait_time -gt "$STARTUP_TIMEOUT" ]; then
			log_error "FAILED: $APP_NAME failed to start up due to timeout $_start_wait_time"
			kill_and_clean_process
			exit 13
		fi
	fi
}



###################################
#(函数)停止程序
###################################
stop() {
	checkpid
	if [ $psid -ne 0 ]; then
		log_info "Stopping $APP_NAME...(pid=$psid) "
		echo "STOPPING" >"$STATE_FILE"
		kill $psid
		_stop_wait_time=0
		# shellcheck disable=SC2046
		until [ $(cat "$STATE_FILE") = "STOP_FAILED" ] || [ $(cat "$STATE_FILE") = "STOPPED" ] || [ $_stop_wait_time -gt "$STOP_TIMEOUT" ]; do
			sleep 5
			log_info "Waiting for $APP_NAME stop ..."
			_stop_wait_time=$((_stop_wait_time + 5))
		done
		_stop_result=$(cat "$STATE_FILE")
		if [ "$_stop_result" = "STOPPED" ]; then
			log_info ": $APP_NAME stopped successfully."
			exit 0
		elif [ "$_start_result" = "STOP_FAILED" ]; then
			log_error "$APP_NAME failed to stop,stop result is $_start_result."
			kill_and_clean_process
			exit 11
		else
			log_error "$APP_NAME failed to start up due to timeout $_start_wait_time, stop result is $_stop_result."
			force_kill_and_clean_process
			exit 12
		fi
	else
		log_info "================================"
		log_warn "$APP_NAME already stopped!"
		rm -f "$STATE_FILE"
		rm -f "$PID_FILE"
		log_info "================================"
	fi

}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
status() {
	checkpid
	if [ $psid -ne 0 ]; then
		log_info "$APP_NAME is running with pid $psid, status is $(cat "$STATE_FILE")"
	else
		log_info "$APP_NAME is not running."
	fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
	log_info "System Information:"
	log_info "****************************"
	log_info "OS Name: $(uname -a)"
	log_line ""
	log_info "JAVA_HOME=$JAVA_HOME"
	log_info "JAVA_VERSION: $("$JAVA_HOME"/bin/java -version)"
	log_line ""
	log_info "APP_ROOT=$APP_ROOT"
	log_info "APP_NAME=$APP_NAME"
	log_info "PROC_NAME=$PROC_NAME"
	log_info "LOG_HOME=$LOG_HOME"
	log_info "PID_FILE=$PID_FILE"
	log_info "STATE_FILE=$STATE_FILE"
	log_line "STARTUP_TIMEOUT=$STARTUP_TIMEOUT"
	log_line "STOP_TIMEOUT=$STOP_TIMEOUT"
	log_info "****************************"
}

###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
'start')
	start
	;;
'stop')
	stop
	;;
'restart')
	stop
	start
	;;
'status')
	status
	;;
'info')
	info
	;;
*)
	echo "Usage: $0 {start|stop|restart|status|info}"
	exit 1
	;;
esac
exit 0
