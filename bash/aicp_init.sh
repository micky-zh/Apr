#!/usr/bin/env bash

DEFAULT_PATH=$(pwd)

ES_CLUSTER="NO"
ES_SERVER_HOST="127.0.0.1:8200"
ES_TEMPLATE_SOURCE="./es_template"

MYSQL_DATABASE="db_create.sql"
MYSQL_INIT="db_init.sql"

LOGSTASH_AICP_SERVER_PATH=""

while [[ $# -gt 0 ]];
do
key="$1"

case $key in
    --init)
    ARG="$2"
    shift # past argument
    shift # past value
    ;;
    --install-path)
    DEFAULT_PATH="$2"
    shift # past argument
    shift # past value
    ;;
    --aicp.server.path)
    LOGSTASH_AICP_SERVER_PATH="$2"
    shift # past argument
    shift # past value
    ;;
    --es.hosts)
    ES_SERVER_HOST="$2"
    shift # past argument
    shift # past value
    ;;
    --es.template.source)
    ES_TEMPLATE_SOURCE="$2"
    shift # past argument
    shift # past value
    ;;
    --mysql.database)
    MYSQL_DATABASE="$2"
    shift # past argument
    shift # past value
    ;;
    --mysql.sql)
    MYSQL_INIT="$2"
    shift # past argument
    shift # past value
    ;;
    --version)
    echo "now version is 1.4"
    exit
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

if [[ -n $1 ]]; then
    echo "Last line of file specified as non-opt/last argument:"
    # tail -1 "$1"
    printf '%s\n' "${POSITIONAL[@]}"
    exit;
fi

function check(){
    if [[ ! -d ${DEFAULT_PATH} ]] ;then
       echo "path: ${DEFAULT_PATH} not exist"
       exit;
    fi

    cd ${DEFAULT_PATH}
    if [ $? -ne 0 ]; then
        echo "init ${ARG} failed"
        exit;
    fi
}
function master_to_master(){
#    #master
#    CREATE USER 'salve'@'%' IDENTIFIED BY 'password';
#    grant replication slave on *.* to 'slave'@'%' identified by 'password';
#
#
#    #slave
#    show slave status;
#    change master to master_host='szth-aip-gold-sales3.szth.baidu.com', master_port=8207,master_user='slave',master_password='password', master_log_file='mysqld-bin.000001',master_log_pos=0;
#    start slave;
#
#    #salve
#    CREATE USER 'salve2'@'%' IDENTIFIED BY 'password';
#    grant replication slave on *.* to 'slave2'@'%' identified by 'password';
#
#    #master
#    show slave status;
#    change master to master_host='szth-aip-gold-sales2.szth.baidu.com', master_port=8207,master_user='slave2',master_password='password', master_log_file='mysqld-bin.000001',master_log_pos=0;
#    start slave;
    echo 1
}
function init_database(){
    # set password for root
    #./bin/mysqladmin -h 127.0.0.1 -u root password 'root4aicp'

    # grant access privileges for root
    #./bin/mysql -h 127.0.0.1 -uroot -proot4aicp mysql --column-names=FALSE --default-character-set=utf8 -B -e "grant all privileges on *.* to 'root'@'%' identified by 'root4aicp'; flush privileges;"
    # create database aicp, tables and user/password for aicp

    if [[ ! -f ${MYSQL_DATABASE} ]] ;then
       echo "file: ${MYSQL_DATABASE} not exist"
       exit;
    fi

    mysql -h 127.0.0.1 -uroot -proot4aicp < ${MYSQL_DATABASE}

    if [[ ! -f ${MYSQL_INIT} ]] ;then
       echo "file: ${MYSQL_INIT} not exist"
       exit;
    fi

    # init data
    mysql -h 127.0.0.1 -uroot -proot4aicp aicp < ${MYSQL_INIT}


    #
    #
    ##创建用户 可以访问的机器 和配置密码
    #CREATE USER 'aicp'@'%' IDENTIFIED BY 'work@!#';
    #
    ##授权用户
    #GRANT ALL ON aicp.* TO 'aicp_user'@'%';
    #
    ##刷新权限
    #FLUSH PRIVILEGES;

    #修改密码
    ##MySQL 5.7.6 and later:
    #ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass';

    ##MySQL 5.7.5 and earlier:
    #SET PASSWORD FOR 'root'@'localhost' = PASSWORD('MyNewPass');

    #删除用户
    #DROP USER 'aicp'@'%';
}

function init_kafka(){
    check
    #kafka 初始化数据
    #########################kafka-config#####################################################################
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic qfaqconversation
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic qkbconversation
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic qtaskBasedConversation
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic qconversation
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic qoperation
    bin/kafka-topics.sh --create --zookeeper localhost:8181 --replication-factor 1 --partitions 40 --topic queue_integration_conversation


    #验证
    bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --group gfaqconversation --topic qfaqconversation --zookeeper localhost:8181
    #########################kafka-config#####################################################################
}

function init_es(){

    if [[ ! -d ${ES_TEMPLATE_SOURCE} ]] ;then
       echo "path: ${ES_TEMPLATE_SOURCE} not exist"
       exit;
    fi

    cd ${ES_TEMPLATE_SOURCE}
    if [ $? -ne 0 ]; then
        exit;
    fi

    curl -XDELETE "http://${ES_SERVER_HOST}/_all"

    # entity
    curl -XPUT "http://${ES_SERVER_HOST}/_template/entity" -d "@entity-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/entity-20180106"
    curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions": [{"add":{"index":"entity-20180106", "alias":"entity"}}]}'

    # conversation
    curl -XPUT "http://${ES_SERVER_HOST}/_template/conversation" -d "@conversation-template.json"
    #curl -XPUT "http://${ES_SERVER_HOST}/conversation-20180106"
    #curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d "{"actions": [{"add":{"index":"conversation-20180106", "alias":"conversation"}}]}"

    # faqconversation
    curl -XPUT "http://${ES_SERVER_HOST}/_template/faqconversation" -d "@faqconversation-template.json"

    # intent
    curl -XPUT "http://${ES_SERVER_HOST}/_template/intent" -d "@intent-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/intent-20180106"
    curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions": [{"add":{"index":"intent-20180106", "alias":"intent"}}]}'


    #document
    curl -XPUT "http://${ES_SERVER_HOST}/_template/operation-history_template" -d '@./operation-history-template.json'
    curl -XPUT "http://${ES_SERVER_HOST}/_template/document_online_template" -d "@./document-online-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/document_online_20180626"
    curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions" : [{ "add" : { "index" : "document_online_20180626", "alias" : "document_online"}}]}'


    #qa
    curl -XPUT "http://${ES_SERVER_HOST}/_template/question_parent_child_template" -d "@./question-parent-child-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/question_parent_child_20180626"
    curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions" : [{ "add" : { "index" :"question_parent_child_20180626", "alias" : "question_parent_child"}}]}'

    #material
    curl -XPUT "http://${ES_SERVER_HOST}/_template/material"  -d "@material-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/material-20180106"
    curl -X POST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions": [{"add":{ "index": "material-20180106", "alias": "material"}}]}'

    #taskbased
    curl -XPUT "http://${ES_SERVER_HOST}/_template/taskbasedconversation" -d "@taskbasedconversation-template.json"

    #property
    curl -XPUT "http://${ES_SERVER_HOST}/_template/property"  -d "@property-template.json"
    curl -XPUT "http://${ES_SERVER_HOST}/property-20180106"
    curl -XPOST "http://${ES_SERVER_HOST}/_aliases" -d '{"actions" : [{ "add" : { "index" : "property-20180106",  "alias" : "property"}}]}'

    #kbconversation
    curl -XPUT "http://${ES_SERVER_HOST}/_template/kbconversation"  -d "@kbconversation-template.json"


    # 闲聊索引
    curl -XPUT "http://${ES_SERVER_HOST}/_template/chat" -d '@chat-template.json'
    curl -X PUT "http://${ES_SERVER_HOST}/chat-20180611"
    curl -X POST "http://${ES_SERVER_HOST}/_aliases"  -d '{"actions" : [{ "add" : { "index" : "chat-20180611", "alias" : "chat" }
    }]}'

    if [[ ${ES_CLUSTER} = "YES" ]]; then
        curl -XPUT  "http://${ES_SERVER_HOST}/_cluster/settings" --data '{"persistent":{"discovery.zen.minimum_master_nodes":2}}'
    fi

#PUT /_cluster/settings
#{
#    "persistent" : {
#        "discovery.zen.minimum_master_nodes" : 2
#    }
#}

}

function init_logstash(){
    check

    if [[ ! -d ${LOGSTASH_AICP_SERVER_PATH} ]] ;then
       echo "path: ${LOGSTASH_AICP_SERVER_PATH} not exist"
       exit;
    fi

    sed -i "s|AICP_SERVER_PATH_REPLACE|${LOGSTASH_AICP_SERVER_PATH}|g" conf/logstash-es.conf.default

    local cluster_host
    while IFS=',' read -ra ADDR; do
      for i in "${ADDR[@]}"; do
          cluster_host+="\"$i\","
      done
    done <<< "$ES_SERVER_HOST"

    sed -i "s|ES_HOST_CLUSTER_REPLACE|${cluster_host%','}|g" conf/logstash-es.conf.default

    cp conf/logstash-es.conf.default conf/logstash-es.conf

    echo "init success"

}

function help_information(){
    me="$(basename "$(test -L "$0" && readlink "$0" || echo "$0")")"
    echo -e "WARN! UNIX need base software: python(2.7.x),dos2unix,gcc(4.6.8+)\n"
    echo -e "usage: aicp.sh -init [params]\n"
    echo -e "--init  \n\t init data."
    echo -e "--es.host  \n\t elastic-search host. port default is 8200"
    echo -e "--es.template.source  \n\t elastic-search index template"
    echo -e "--aicp.server.path  \n\t aicp server install path"
    echo -e "--mysql  \n\t aicp server install path"

    echo -e "\ninit example:"
    echo -e "\tkafka:\n\t\tsh $me --init kafka --install-path /home/work/local/kafka_2.10-0.10.2.1"
    echo -e "\tes:\n\t\tsh $me --init es  --es.hosts 10.0.0.2:8200 --es.template.source /home/work/es_template"
    echo -e "\tlogstash:\n\t\tsh $me --init logstash  --install-path /home/work/local/logstash-5.5.0 --aicp.server.path /home/work/local/aicp-api --es.hosts 10.0.0.1:8200"
    echo -e "\tmysql:\n\t\tsh $me --init mysql --mysql.database ./db_create.sql --mysql.sql ./db_init.sql"

}

function init(){

    if [[ ( -z $ARG ) ]]; then
        help_information;
        exit;
    fi


    if [[ $ARG = 'kafka' ]]; then
        init_kafka;
    elif [[ $ARG = 'mysql' ]]; then
        init_database
    elif [[ $ARG = 'es' ]]; then
        init_es
    elif [[ $ARG = 'logstash' ]]; then
        init_logstash
    else
        echo "not matched params ${ARG}"
        exit
    fi
}

if [[ -n $ARG ]] ; then
    init
    exit;
fi
    help_information


##意图初始化
#curl -i "127.0.0.1:4830/api/v1/intents/sys_intent_rebuild?version=20171212" -H 'Authorization: AICP 7cff0d5a-b5e2-4aed-8094-03c8561b626b'

#redis配置
#############################################redis-server################################################
#echo 2048 > /proc/sys/net/core/somaxconn
#
#修改文件 /etc/sysctl.conf
#vm.overcommit_memory = 1
#
#退出后重新登录 执行如下命令验证
#sysctl vm.overcommit_memory=1

### elastic-head
#vi Gruntfile.js，修改端口，大概在第93行
#启动：./node_modules/grunt/bin/grunt server

#############################################redis-server################################################


#curl -i "127.0.0.1:8830/api/v1/intents/sys_intent_rebuild?version=20171212" -H 'Authorization: AICP xxxxx'
#curl -XPOST "127.0.0.1:8830/api/v1/chitchat/rebuild?version=20171212&rebuild=all" -H 'Authorization: AICP xxxxx'

