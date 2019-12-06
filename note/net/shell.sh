#!/usr/bin/env bash

# 获取所有的ip地址
ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1'

#  on the server side
iperf -s -p 8472 -u

# on the client side
iperf -c 172.28.128.103 -u -p 8472 -b 1K