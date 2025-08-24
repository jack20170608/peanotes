#!/usr/bin/env bash

mvn clean package
scp -r target/host-helper-server*with-dependencies.jar dev5:/home/jack/apps/host-helper-server/
scp bin/start.sh dev5:/home/jack/apps/host-helper-server/

