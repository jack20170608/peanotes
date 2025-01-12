#!/usr/bin/env bash

echo "test with curl command"

### 1.1 call the idle beat
seq 3 | while read line; do \
   curl -is -X GET -H 'accept: text/plain' 'http://localhost:12580/foo/task/v1/beat' \
; done


### 2.1
seq 3 | while read line; do \
  curl -is -X POST -H 'accept: application/json' -H 'content-type: application/json' --data '' 'http://localhost:12580/foo/task/v1/idleBeat' \
; done

