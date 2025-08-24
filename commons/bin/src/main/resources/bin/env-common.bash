#!/usr/bin/env bash

__SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
source "${__SCRIPT_DIR}"/common/log.bash

declare -a ALL_MANDANTORY_ENV_VARS=(
    "APP_NAME"
    "JAVA_HOME"
    "LOG_DIR"
    "STARTUP_TIMEOUT"
    "PROC_NAME"
)

# make sure all mandatory environment variables are set
function check_mandantory_env_vars() {
    for var in "${ALL_MANDANTORY_ENV_VARS[@]}"; do
        if [ -z "${!var}" ]; then
            log_error "Error: Environment mandatory variable [$var] is not set."
            exit 1
        fi
    done
}

function print_mandantory_env_vars() {
    log_info "All mandatory environment variables set below."
    for var in "${ALL_MANDANTORY_ENV_VARS[@]}"; do
        log_info "-- $var=${!var}"
    done
}



