#!/usr/bin/env bash
source ~/.bashrc

POSITIONAL=()
DEFAULT_PATH=/home/work/local
INSTALL=NO
ARG=''
TOOL_NAME=
PKG_PATH=
CASE_ENV='NO'
FORCE='NO'

SERVER_ID=1
KAFKA_SERVER_HOST=$HOSTNAME
ZOOKEEPER_SERVER_CLUSTER=$HOSTNAME

ES_SERVER_CLUSTER=$HOSTNAME
ES_JVM='1g'
ES_PLUGIN_ANS4J='NO'

MYSQL_AUTO_INCREMENT_OFFSET=1
################################ To be continued  ################################

KAFKA_PORT=8092
ZOOKEEPER_PORT=8181

ES_HTTP_PORT=8200
ES_TCP_PORT=8200

################################ To be continued  ################################

while [[ $# -gt 0 ]];
do
key="$1"

case $key in
    -new|--install)
    ARG="$2"
    INSTALL="YES"
    shift # past argument
    shift # past value
    ;;
    -p|--path)
    DEFAULT_PATH="$2"
    shift # past argument
    shift # past value
    ;;
    --force)
    FORCE="YES"
    shift # past value
    ;;
    --env)
    CASE_ENV="YES"
    shift # past argument
    ;;
    --sysenv)
    echo "export LC_ALL=zh_CN.UTF-8" >> ~/.bashrc
    shift # past argument
    ;;
    --mysql-master-A)
    MYSQL_AUTO_INCREMENT_OFFSET=1
    shift # past argument
    ;;
    --mysql-master-B)
    MYSQL_AUTO_INCREMENT_OFFSET=2
    shift # past argument
    ;;
    --server.id)
    SERVER_ID="$2"
    shift # past argument
    shift # past value
    ;;
    --zookeeper.hosts)
    ZOOKEEPER_SERVER_CLUSTER="$2"
    shift # past argument
    shift # past value
    ;;
    --es.hosts)
    ES_SERVER_CLUSTER="$2"
    shift # past argument
    shift # past value
    ;;
    --es.jvm)
    ES_JVM="$2"
    shift # past argument
    shift # past value
    ;;
    --with-plugin-ans4j)
    ES_PLUGIN_ANS4J="YES"
    shift # past argument
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


function install_package(){
    if [[ ${FORCE} = "YES" ]]; then
        rm -rf $1/$2
    fi

    if [[ -a $1/$2 ]]; then
        echo "path $1/$2 already exist!"
        echo "install $2 failed!"
        echo "tip: use --force will be remove package. please backup the files before remove it."
        exit;
    fi

    [[ ! -d ${DEFAULT_PATH} ]] && mkdir -p ${DEFAULT_PATH}

    mv $2 $1/

    check;

    echo -e "path: $1/$2"
}

function check(){
    if [ $? -ne 0 ]; then
        echo "install ${TOOL_NAME} failed"
        exit;
    fi
}

function init_front(){

    if [[ $CASE_ENV = "YES" ]]; then
        TOOL_NAME=webenv
        tar -xzf webenv.node.tar.gz
        check
        mv webenv ${DEFAULT_PATH}/ 2>/dev/null

        echo "export NODE_PATH=${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/lib/node_modules" >> ~/.bashrc
        echo "export PATH=\$PATH:${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/bin" >>  ~/.bashrc
    fi

    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"

    TOOL_NAME=aicp-web
    unzip -q aicp-web.zip
    check

    install_package ${DEFAULT_PATH} ${TOOL_NAME}
    echo -e "install ${TOOL_NAME} success!\n"
    echo -e "start server: cd aicp-web && pm2 start app.js -n cmb\n\n"

}

function init_node(){

    TOOL_NAME=webenv
    tar -xzf webenv.node.6.12.2.tar.gz
    check

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    if [[ $CASE_ENV = "YES" ]]; then
        echo "export NODE_PATH=${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/lib/node_modules" >> ~/.bashrc
        echo "export PATH=\$PATH:${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/bin" >>  ~/.bashrc
    fi

    echo -e "install ${TOOL_NAME} success!\n"
}



function init_server(){
    TOOL_NAME=aicp-api
    unzip -qo ${TOOL_NAME}-dist.zip
    check;

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    mkdir -p ${DEFAULT_PATH}/${TOOL_NAME}/model

    if [[ -e model.vec.zip ]]; then
        unzip model.vec.zip
    fi

    cp model.vec ${DEFAULT_PATH}/${TOOL_NAME}/model/

    check;

    echo -e "path: ${DEFAULT_PATH}/${TOOL_NAME}"
    echo -e "install ${TOOL_NAME} success!\n"
}


function init_es_config(){
    local cluster_host
    while IFS=',' read -ra ADDR; do
      for i in "${ADDR[@]}"; do
          cluster_host+="\"$i:8300\","
      done
    done <<< "$ES_SERVER_CLUSTER"
    echo "discovery.zen.ping.unicast.hosts: [${cluster_host%','}]" >> ${TOOL_NAME}/config/elasticsearch.yml

    #jvm
    sed -i "s/^-Xms.*g/-Xms${ES_JVM}/g" ${TOOL_NAME}/config/jvm.options
    sed -i "s/^-Xmx.*g/-Xmx${ES_JVM}/g" ${TOOL_NAME}/config/jvm.options

    echo "#!/bin/bash" > ${TOOL_NAME}/start.sh
    echo "sh bin/elasticsearch -d -p es.pid" >> ${TOOL_NAME}/start.sh
    chmod u+x ${TOOL_NAME}/start.sh
}

function generate_es_start(){

    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/bin/*

    echo "#!/bin/bash" > ${DEFAULT_PATH}/${TOOL_NAME}/start.sh
    echo "sh bin/elasticsearch -d -p es.pid" >> ${DEFAULT_PATH}/${TOOL_NAME}/start.sh
    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/start.sh

    echo "#!/bin/bash" > ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh
    echo "kill \`cat es.pid\`" >> ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh
    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh

}

function init_es(){
    TOOL_NAME=elasticsearch-5.2.2
    unzip -qo elasticsearch-5.2.2.zip
    check;

    init_es_config;
    check

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    if [[ ${ES_PLUGIN_ANS4J} = "YES" ]]; then
        sh ${DEFAULT_PATH}/${TOOL_NAME}/bin/elasticsearch-plugin install file://${DEFAULT_PATH}/${TOOL_NAME}/elasticsearch-analysis-ansj-5.2.2.0-release.zip

        if [[ ( -n ${JAVA_HOME} ) ]]; then
            cat ${DEFAULT_PATH}/${TOOL_NAME}/plugin-security.policy >> ${JAVA_HOME}/jre/lib/security/java.policy
        else
            echo "please add 'plugin-security.policy' to system java_home/jre/lib/security/java.policy"
        fi
    fi
    generate_es_start;
    check;

    echo -e "install ${TOOL_NAME} success!\n"
}

function init_java(){
    TOOL_NAME=jdk1.8.0_151
    tar -xzf ${TOOL_NAME}.tar.gz

    check;

    if [[ $CASE_ENV = "YES" ]]; then
        echo "export JAVA_HOME=${DEFAULT_PATH}/${TOOL_NAME}" >> ~/.bashrc
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    fi

    install_package ${DEFAULT_PATH} ${TOOL_NAME}
    echo -e "install ${TOOL_NAME} success!\n"
}

function generate_mysql_start(){
    # stop.sh
    echo "#!/bin/bash" > ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh
    echo "kill \`cat ${DEFAULT_PATH}/${TOOL_NAME}/mysql/mysqld.pid\`" >> ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh

    # start.sh
    echo "#!/bin/bash" > ${DEFAULT_PATH}/${TOOL_NAME}/start.sh
    echo "nohup bin/mysqld_safe --defaults-file=conf/my.cnf &" >> ${DEFAULT_PATH}/${TOOL_NAME}/start.sh

    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/start.sh
    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/stop.sh
    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/bin/*
}

function init_mysql(){
    TOOL_NAME=mysql-5.6.38
    #tar -xzf ${TOOL_NAME}.tar.gz
    unzip -qo ${TOOL_NAME}.zip
    check;

    sed -i "s|MYSQL_INSTALL_PATH|${DEFAULT_PATH}|g" ${TOOL_NAME}/conf/my.cnf
    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    generate_mysql_start


    if [[ $CASE_ENV = "YES" ]]; then
        echo "export MYSQL_HOME=${DEFAULT_PATH}/$2/${TOOL_NAME}" >> ~/.bashrc
        echo "export PATH=\$PATH:\$MYSQL_HOME/bin" >>  ~/.bashrc
    fi

    echo -e "install ${DEFAULT_PATH} success!\n"
}


function init_mysql_cluster(){
    TOOL_NAME=mysql-5.6.38
    #tar -xzf ${TOOL_NAME}.tar.gz
    unzip -qo ${TOOL_NAME}.zip
    check;

    sed -i "s|MYSQL_INSTALL_PATH|${DEFAULT_PATH}|g" ${TOOL_NAME}/conf/my.cnf.cluster
    sed -i "s|MYSQL_SERVER_ID|${SERVER_ID}|g" ${TOOL_NAME}/conf/my.cnf.cluster
    sed -i "s|MYSQL_AUTO_INCREMENT_OFFSET|${MYSQL_AUTO_INCREMENT_OFFSET}|g" ${TOOL_NAME}/conf/my.cnf.cluster
    mv  ${TOOL_NAME}/conf/my.cnf.cluster ${TOOL_NAME}/conf/my.cnf


    install_package ${DEFAULT_PATH} ${TOOL_NAME}


    generate_mysql_start

    if [[ $CASE_ENV = "YES" ]]; then
        echo "export MYSQL_HOME=${DEFAULT_PATH}/$2/${TOOL_NAME}" >> ~/.bashrc
        echo "export PATH=\$PATH:\$MYSQL_HOME/bin" >>  ~/.bashrc
    fi

    echo -e "install ${DEFAULT_PATH} success!\n"
}

function init_logstash(){
    TOOL_NAME=logstash-5.5.0
    #tar -xzf logstash-5.5.0.tar.gz
    unzip -qo logstash-5.5.0.zip
    check;

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/bin/*

    echo -e "install ${TOOL_NAME} success!\n"
}

function init_redis(){
    TOOL_NAME=redis-3.2.8
    unzip -qo redis-3.2.8.zip

    check;

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    echo -e "install ${TOOL_NAME} success!"
    echo -e "start command: nohup ./src/redis-server redis.conf &\n"
}


function init_kafka_config(){

    #kafka
    sed -i "s|SERVER_ID_FOR_REPLACE|${SERVER_ID}|g" ${TOOL_NAME}/config/server.properties
    sed -i "s|listeners=PLAINTEXT://:8092|listeners=PLAINTEXT://${KAFKA_SERVER_HOST}:8092|g" ${TOOL_NAME}/config/server.properties


    #zookeeper
    echo ${SERVER_ID} > ${TOOL_NAME}/data/zookeeper/myid

    local count=1
    local zoo_host
    while IFS=',' read -ra ADDR; do
      for i in "${ADDR[@]}"; do
          echo "server.$count=$i:2888:3888" >> ${TOOL_NAME}/config/zookeeper.properties
          zoo_host+="$i:8181,"
          count=`expr ${count} + 1`
      done
    done <<< "$ZOOKEEPER_SERVER_CLUSTER"
    echo "zookeeper.connect=${zoo_host%','}" >> ${TOOL_NAME}/config/server.properties


}

function init_kafka(){
    TOOL_NAME=kafka_2.10-0.10.2.1
    unzip -qo kafka_2.10-0.10.2.1.zip

    check;

    init_kafka_config;

    install_package ${DEFAULT_PATH} ${TOOL_NAME}

    chmod u+x ${DEFAULT_PATH}/${TOOL_NAME}/bin/*

    echo -e "install ${TOOL_NAME} success!\n"
}




function prompt(){
    read -p "Are you sure? " -n 1 -r
    echo    # (optional) move to a new line
    if [[ $REPLY =~ ^[Yy]$ ]];then
        return 0
    echo
        # do dangerous stuff
        return 1
    fi
}

function help_information(){
    me="$(basename "$(test -L "$0" && readlink "$0" || echo "$0")")"
    echo -e "usage: aicp.sh -new [params]\n"
    echo -e "-p,--path \n\tinstall path,will be used."
    echo -e "-new,--install \n\tjava,aicp,front,logstash,kafka,redis,es.eg"
    echo -e "--env \n\twill set tool's env. like \$JAVA_HOME"
    echo -e "--sysenv \n\twill set system env. like \$LANG"
    echo -e "--force \n\twill remove target dir package."
    echo -e "--version \n\tshow current version info."
    echo -e "--server.id \n\tset server id"
    echo -e "--zookeeper.hosts \n\tset zookeeper's cluster hosts. default is \$HOSTNAME "
    echo -e "--es.hosts \n\tset es's cluster hosts. default is \$HOSTNAME "
    echo -e "--es.jvm \n\tset es java heap size. default is 1g "
    echo -e "\ninstall example:"
    echo -e "\tkafka:\n\t\tsh $me --install kafka --path /home/work/local"
    echo -e "\tjava:\n\t\tsh $me --install java --path /home/work/local --env --sysenv"
    echo -e "\tmysql:\n\t\tsh $me --install mysql --path /home/work/local --env"
    echo -e "\tnodeJs:\n\t\tsh $me --install node --path /home/work/local --env "
    echo -e "\tredis:\n\t\tsh $me --install redis --path /home/work/local "
    echo -e "\tnginx:\n\t\tsh $me --install nginx --path /home/work/local "
    echo -e "\tlogstash:\n\t\tsh $me --install logstash --path /home/work/local "
    echo -e "\telastic-search:\n\t\tsh $me --install es --path /home/work/local --es.hosts x.x.x.x --es.jvm 1g --with-plugin-ans4j"
    echo -e "\tAicp server:\n\t\tsh $me --install aicp --path /home/work/local "
    echo -e "\tAicp front:\n\t\tsh $me --install front --path /home/work/local "
    echo -e "\ncluster example:"
    echo -e "\tmysql-cluster:\n\t\tsh $me --install mysql-cluster --mysql-master-A --server.id 1 --path /home/work/local --env"
    echo -e "\t\tsh $me --install mysql-cluster --mysql-master-B --server.id 2 --path /home/work/local --env"
    echo -e "\tkafka-cluster:\n\t\tsh $me --install kafka --path /home/work/local --server.id 1 --zookeeper.hosts 10.18.134.2,10.18.134.3"

}

function install(){

    if [[ ( -z $ARG )  && ($INSTALL != "YES") ]]; then
        help_information;
        exit;
    fi


    if [[ $ARG = 'java' ]]; then
        init_java;
    elif [[ $ARG = 'mysql' ]]; then
        init_mysql
    elif [[ $ARG = 'mysql-cluster' ]]; then
        init_mysql_cluster
    elif [[ $ARG = 'aicp' ]]; then
        init_server
    elif [[ $ARG = 'front' ]]; then
        init_front
    elif [[ $ARG = 'redis' ]]; then
        init_redis
    elif [[ $ARG = 'es' ]]; then
        init_es
    elif [[ $ARG = 'logstash' ]]; then
        init_logstash
    elif [[ $ARG = 'kafka' ]]; then
        init_kafka
    elif [[ $ARG = 'node' ]]; then
        init_node
    elif [[ $ARG = 'all' ]]; then
        init_java
        init_mysql
        init_es
        init_redis
        init_kafka
        init_logstash
        init_server
        init_front

    else
        echo "not matched params ${ARG}"
        exit
    fi
}

if [[ -n $ARG ]] ; then
    install
    exit;
fi
    help_information

# echo ARG = "${ARG}"
# echo DEFAULT = "${DEFAULT}"
# echo "Number files in SEARCH PATH with EXTENSION:" $(ls -1 "${SEARCHPATH}"/*."${EXTENSION}" | wc -l)

