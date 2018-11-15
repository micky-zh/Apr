#!/usr/bin/env bash

CLUSTER_HOST="127.0.0.1:6441 127.0.0.1:6442 10.233.28.38:6441 10.233.28.38:6442 10.233.28.39:6441 10.233.28.39:6442"
while IFS=' ' read -ra ADDR; do
  for i in "${ADDR[@]}"; do
       HOST=${i%:*}
       PORT=${i##*:}
      ./bin/redis-cli -p ${PORT} -h ${HOST} cluster nodes
      ./bin/redis-cli -p ${PORT} -h ${HOST} cluster reset
  done
done <<< "$CLUSTER_HOST"

