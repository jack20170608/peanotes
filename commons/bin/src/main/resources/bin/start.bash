#!/usr/bin/env bash

__SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
__SCRIPT_NAME=$(basename "$0")
__SCRIPT_VERSION="1.0.0"
__SCRIPT_AUTHOR="jack"

source "${__SCRIPT_DIR}"/common/log.sh

# The flag to indicate whether to start up all nodes
FLAG_ALL=false
# The flag to indicate whether to start up specific node
FLAG_NODE=false
# the internal flag used
FLAG_INTERNAL=false
# The name of the node to start up
NODE_NAME=DEFAULT

# The help function
function help() {
	echo "Name: $__SCRIPT_NAME"
	echo "Version: $__SCRIPT_VERSION"
	echo "Author: $__SCRIPT_AUTHOR"
	echo "Usage: $__SCRIPT_NAME [-a] [-n <cur_node_name>] [-h]"
	echo "Options:"
	echo "  -a      Start up all nodes"
	echo "  -n      Start up specific node with given name"
	echo "  -h      Display this help message"
}

# parse the command args
while getopts ":ahzn:" opt; do
	case $opt in
	a)
		FLAG_ALL=true
		;;
	n)
		FLAG_NODE=true
		NODE_NAME=$OPTARG
		;;
	z)
		FLAG_INTERNAL=true
		;;
	h)
		help
		exit 0
		;;
	\?)
		log_error "Invalid option: -$OPTARG" >&2
		echo ""
		help
		exit 1
		;;
	:)
		log_error "Option -$OPTARG requires an argument." >&2
		help
		exit 1
		;;
	esac
done

if [ "$FLAG_ALL" != "true" ] && [ "$FLAG_NODE" != "true" ]; then
	log_error "Please specify -a or -n option"
	help
	exit 1
fi

if [ "$FLAG_ALL" == "true" ] && [ "$FLAG_NODE" == "true" ]; then
	log_error "Please specify either -a or -n option, not both"
	help
	exit 1
fi

# shellcheck disable=SC2155
export APP_ROOT=$(cd "$(__SCRIPT_DIR)/../" && pwd -P)
source "${__SCRIPT_DIR}"/env-common.bash

log_info "Starting with APP_ROOT=$APP_ROOT"
log_line ""
ENV_NAME=$(cat "$APP_ROOT"/config/environment.cfg)
log_info "Environment name: $ENV_NAME"
source "$APP_ROOT"/config/setenv-common

if [ -f "$APP_ROOT"/config/setenv-"${ENV_NAME}" ]; then
	# shellcheck disable=SC1090
	source "$APP_ROOT"/config/setenv-"${ENV_NAME}"
fi

## The startup config file
START_CONFIG_PATH="$APP_ROOT/config/startup.cfg"

## The user to run as, by default it's the current user
SSH_USER=$(id -un)

if [ "$FLAG_INTERNAL" != "true" ]; then
	## Check if the file exists
	if [ -f "$START_CONFIG_PATH" ]; then
		# shellcheck disable=SC2095
		while IFS= read -r line; do
			IFS=',' read -r -a items <<<"$line"
			cur_node_name="${items[0]}"
			cur_host_name="${items[1]}"
			cur_app_path="${items[2]}"

			ssh_result=
			ssh_exit_code=

			if [ "$FLAG_ALL" == "true" ] || [ "$FLAG_NODE" == "true" ] || [ "$NODE_NAME" == "$cur_node_name" ]; then
				log_info "Starting app with node=$cur_node_name on host $cur_host_name"
				# 构建远程命令数组
				remote_commands=(
					"cd $cur_app_path/bin"
					"&&"
					"./start.sh -n $cur_node_name -z"
				)
				# 将数组元素拼接成完整的命令字符串
				remote_cmd=$(printf "%s " "${remote_commands[@]}")
				# 使用 printf 的 %q 转义命令，防止特殊字符导致解析错误
				printf -v escaped_remote_cmd '%q' "$remote_cmd"
				# 构建最终的 ssh 命令
				ssh_result=$(ssh -o StrictHostKeyChecking=no "$SSH_USER"@"$cur_host_name" "bash -c $escaped_remote_cmd")
				ssh_exit_code=$?

				## Print the result
				if [ $ssh_exit_code -ne 0 ]; then
					log_error "Error starting app on host $cur_host_name"
					log_error "Result: $ssh_result"
					log_error "Exit code: $ssh_exit_code"
					exit 1
				else
					log_info "App started on host $cur_host_name"
				fi
			fi
		done <"$START_CONFIG_PATH"
	else
		log_error "The startup config file does not exist: $START_CONFIG_PATH"
		exit 1
	fi
fi

check_mandantory_env_vars

## Check if PID already exists
PID_FILE="$LOG_DIR/$PROC_NAME.pid"
STATE_FILE="$LOG_DIR/$PROC_NAME.state"

if [ -f "$PID_FILE" ]; then
	KNOWN_PID=$(cat "$PID_FILE")
	KNOWN_PID_CHECK_RESULT=
	if [ -n "$KNOWN_PID" ]; then
		KNOWN_PID_CHECK_RESULT=$(pgrep -f -x -p "$KNOWN_PID" "$PROC_NAME" | grep -cv -E "start.bash|stop.bash|head|tail|grep")
		if [ "$KNOWN_PID_CHECK_RESULT" -gt 0 ]; then
			log_error "PID $KNOWN_PID already exists for process $PROC_NAME"
			exit 1
		fi
	fi
else
	SEARCH_PID=$(pgrep -fa "$PROC_NAME" | grep -Ev "start.bash|head|tail|grep" | cut -d ' ' -f1)
	SEARCH_PID_CHECK_RESULT=$(pgrep -fa "$PROC_NAME" | grep -cv -E "start.bash|stop.bash|head|tail|grep")
	if [ "$SEARCH_PID_CHECK_RESULT" -gt 0 ]; then
		log_error "Process $PROC_NAME already exists with PID $SEARCH_PID"
		exit 1
	fi
fi

log_info "Starting $PROC_NAME on $HOSTNAME"
log_line "Starting"

JVM_ARGS=
for ((i = 1; i < 200; i++)); do
	cur_var="JVM_ARG_$i"
	if [ -n "${!cur_var}" ]; then
		JVM_ARGS="$JVM_ARGS ${!cur_var}"
	fi
done

echo "" >"$STATE_FILE"
CONSOLE_LOG="$LOG_DIR/$PROC_NAME-console.log"

start_command_token=(
	"nohup $JAVA_HOME/bin/java -LOG_HOME=$LOG_DIR"
	"-Dlog4j.configurationFile=$LOG_CONFIG_FILE"
	"-Dapp.name=$APP_NAME"
	"-Dproc.name=$PROC_NAME"
	"$JVM_ARGS"
	"-jar $JAR_FILE $JAR_ARGS"
	"> $CONSOLE_LOG 2>&1 &"
)
start_command=$(printf "%s " "${start_command_token[@]}")
log_info "start command: $start_command"

eval "$start_command"
CURRENT_PID=$!


