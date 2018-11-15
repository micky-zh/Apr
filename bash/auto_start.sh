#!/usr/bin/env bash

source /etc/profile
source /etc/bashrc
source ~/.bash_profile

BASE_DIR=$PWD
ARG=$1


function listen_aicp_server(){

    PID=`ps aux | grep -v grep|grep java| grep ${BASE_DIR}/aicp-api.jar`

    if [[ -z $PID ]]; then
        sh bin/stop.sh
        sh bin/start.sh
    fi
}

function listen_mysql(){

    PID=`ps aux | grep -v grep| grep ${BASE_DIR}/bin/mysqld_safe`

    if [[ -z ${PID} ]]; then
        sh stop.sh
        sh start.sh
    fi
}

function listen_log_stash(){

    PID=`ps aux | grep -v grep| grep java|grep 'logstash-5.5.0'`

    if [[ -z ${PID} ]]; then
        sh start.sh
    fi
}

function listen_elastic_search(){

    PID=`ps aux | grep -v grep| grep 'java' | grep 'org.elasticsearch.Elasticsearch'`

    if [[ -z ${PID} ]]; then
        sh stop.sh
        sh start.sh
    fi
}

function listen_kafka(){

    PID=`ps aux | grep -v grep| grep 'java' |  grep 'kafka.Kafka'`
    if [[ -z ${PID} ]]; then
        sh start-kafka.sh
    fi
}
function listen_zookeeper(){
    PID=`ps aux | grep -v grep| grep 'java' |  grep 'org.apache.zookeeper.server.quorum.QuorumPeerMain'`
    if [[ -z ${PID} ]]; then
        sh start-zookeeper.sh
    fi
}

if [[ $ARG = 'aicp-server' ]]; then
    listen_aicp_server
elif [[ $ARG = 'mysql' ]]; then
    listen_mysql
elif [[ $ARG = 'aicp-web' ]]; then
    init_server
elif [[ $ARG = 'redis' ]]; then
    init_redis
elif [[ $ARG = 'es' ]]; then
    listen_elastic_search
elif [[ $ARG = 'logstash' ]]; then
    listen_log_stash
elif [[ $ARG = 'kafka' ]]; then
    listen_kafka
elif [[ $ARG = 'zookeeper' ]]; then
    listen_zookeeper
else
    echo "not matched params ${ARG}, should be in: aicp-server,aicp-web,mysql,es,kafka,zookeeper"
    echo "usage: crontab -e"
    echo "*/1 * * * * cd server_path && sh auto_start.sh"
    exit
fi