#!/bin/bash
while IFS='' read -r line || [[ -n "$line" ]]; do
#    lc=`grep ${line} lc.all`
#    if [[ -n $ ${lc} ]]; then
#        echo ${lc} >> sum.txt
#    fi
     bin/mysql -uaicp_w -pxxx -h10.232.19.42 -D aicp -e 'select user_id from agent where id=${line}' -N -B
done < "$1"