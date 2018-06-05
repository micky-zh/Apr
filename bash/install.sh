#!/usr/bin/env bash
POSITIONAL=()
DEFAULT_PATH=/home/work/local
INSTALL=NO
ARG=''
TOOL_NAME=
CASE_ENV='NO'

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
    --env)
    CASE_ENV="YES"
    shift # past argument
    ;;
    --sysenv)
    echo "export LC_ALL=zh_CN.UTF-8" >> ~/.bashrc
    shift # past argument
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
    if [ $? -eq 0 ]; then
        echo "install ${TOOL_NAME} success"
    else
        echo "install ${TOOL_NAME} failed"
        exit;
    fi
}

function init_front(){
    TOOL_NAME=webenv
    tar -xzf webenv.node.tar.gz
    check
    mv webenv ${DEFAULT_PATH}/ 2>/dev/null

    if [[ $CASE_ENV = "YES" ]]; then
        echo "export NODE_PATH=${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/lib/node_modules" >> ~/.bashrc
        echo "export PATH=\$PATH:${DEFAULT_PATH}/webenv/node-v6.12.2-linux-x64/bin" >>  ~/.bashrc
    fi

    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"

    TOOL_NAME=aicp-web
    unzip -q aicp-cmb-v3.0.zip
    check
    mv aicp-cmb-v3.0 ${DEFAULT_PATH}/aicp-web 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
    echo "start server: cd aicp-web && pm2 start app.js -n cmb"

}


function init_server(){
    TOOL_NAME=aicp-api
    unzip -q ${TOOL_NAME}-dist.zip
    check;
    mv ${TOOL_NAME} ${DEFAULT_PATH}/ 2>/dev/null
    mkdir -p ${DEFAULT_PATH}/${TOOL_NAME}/model
    mv model.vec ${DEFAULT_PATH}/${TOOL_NAME}/model/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
}


function init_es(){
    TOOL_NAME=elasticsearch-5.2.2
    unzip -q elasticsearch-5.2.2.zip
    check;
    mv elasticsearch-5.2.2 ${DEFAULT_PATH}/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
}

function init_java(){
    TOOL_NAME=jdk1.8.0_151
    tar -xzf ${TOOL_NAME}.tar.gz
    check;

    if [[ $CASE_ENV = "YES" ]]; then
        echo "export JAVA_HOME=${DEFAULT_PATH}/${TOOL_NAME}" >> ~/.bashrc
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    fi

    mv ${TOOL_NAME} ${DEFAULT_PATH}/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
}

function init_mysql(){
    TOOL_NAME=mysql-5.6.38
    tar -xzf ${TOOL_NAME}-green.tar.gz
    check;
    echo "warning: force will be installed : $HOME "
    mv mysql-5.6.38 ${HOME}/ 2>/dev/null
    echo "path: ${HOME}/${TOOL_NAME}"
}

function init_logstash(){
    TOOL_NAME=logstash-5.5.0
    tar -xzf logstash-5.5.0.tar.gz
    check;
    mv logstash-5.5.0 ${DEFAULT_PATH}/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
}

function init_redis(){
    TOOL_NAME=redis-3.2.8
    unzip -q redis-3.2.8.zip
    check;
    mv redis-3.2.8 ${DEFAULT_PATH}/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
    echo "start command: nohup ./src/redis-server redis.conf &"
}

function init_kafka(){
    TOOL_NAME=kafka_2.10-0.10.2.1
    tar -xzf kafka_2.10-0.10.2.1.zip
    check;
    mv kafka_2.10-0.10.2.1 ${DEFAULT_PATH}/ 2>/dev/null
    echo "path: ${DEFAULT_PATH}/${TOOL_NAME}"
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
    echo "usage: aicp.sh -new [params]"
    echo -e "-p,--path \n\tinstall path,will be used."
    echo -e "-new,--install \n\tjava,aicp,front,logstash,kafka,redis,es.eg"
    echo -e "--env \n\twill set tool's env. like JAVA_HOME"
    echo -e "--sysenv \n\twill set system env. like \$LANG"
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
    elif [[ $ARG = 'all' ]]; then
        init_server
        init_front
        init_java
        init_mysql
        init_es
        init_redis
        init_kafka
        init_logstash
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

