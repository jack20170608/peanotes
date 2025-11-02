#!/usr/bin/env bash

rls_version=$1
app_root="/home/jack/apps/"

mvn clean package
ssh -q dev5 "mkdir -pv ${app_root}/host-helper-server/$rls_version"
ssh -q dev5 "echo -n sit > ${app_root}/host-helper-server/$rls_version/config/environment.cfg"

scp -r target/*-service.tar.gz dev5:/home/jack/apps/host-helper-server/$rls_version/

ssh -q dev5 "cd ${app_root}/host-helper-server/$rls_version/ && tar -zxvf *-service.tar.gz && rm *-service.tar.gz"

ssh -q dev5 "cd ${app_root}/host-helper-server/$rls_version/ && ./bin/app.bash restart "

