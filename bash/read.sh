#!/bin/bash
while IFS='' read -r line || [[ -n "$line" ]]; do
#    lc=`grep ${line} lc.all`
#    if [[ -n $ ${lc} ]]; then
#        echo ${lc} >> sum.txt
#    fi
     echo $line
done < "$1"