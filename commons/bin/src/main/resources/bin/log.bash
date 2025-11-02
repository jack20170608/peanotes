#!/usr/bin/env bash


__CN='\033[0m';__CB='\033[0;30m';__CR='\033[0;31m';__CG='\033[0;32m';
__CY='\033[0;33m';__CB='\033[0;34m';__CM='\033[0;35m';__CC='\033[0;36m';__CW='\033[0;37m';
function log_info() {  printf "[${__CG} OK ${__CN}][$(date '+%F %T')]${__CG}$*${__CN}\n";   }
function log_warn() {  printf "[${__CY}WARN${__CN}][$(date '+%F %T')]${__CY}$*${__CN}\n";   }
function log_error() { printf "[${__CR}ERROR${__CN}][$(date '+%F %T')]${__CR}$*${__CN}\n";   }
function log_debug() { printf "[${__CB}DEBUG${__CN}][$(date '+%F %T')]${__CB}$*${__CN}\n"; }
function log_hint()  { printf "[${__CB}HINT${__CN}][$(date '+%F %T')]${__CB}$*${__CN}\n"; }
function log_line()  { printf "${__CM}[$*] ===========================================${__CN}\n"; }


