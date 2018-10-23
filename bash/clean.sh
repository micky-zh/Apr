#!/bin/bash
DAYS=`date +%Y-%m-%d' '%H:%M:%S`
days=14
if [ $1"x" = "x" ]
then
	REMOVE_DAYS_ago1=`date -d 14-days-ago +%Y%m%d`
	REMOVE_DAYS_ago2=`date -d 14-days-ago +%Y-%m-%d`
else
	REMOVE_DAYS_ago1=`date -d $1-days-ago +%Y%m%d`
        REMOVE_DAYS_ago2=`date -d $1-days-ago +%Y-%m-%d`
	days=$1
fi
echo "DATE : $DAYS" > /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
echo " " >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
ps aux|grep java|while read PRO_JAVA
do
        for INFO_JAVA in $PRO_JAVA
        do
                location_java=`echo $INFO_JAVA|grep Dcatalina.home`
                if [ $location_java"x" != "x" ]
                then
                        LOCATION_JAVA=`echo $location_java|awk -F '=' '{print $2}'`
                        cd $LOCATION_JAVA/logs
			dir=`pwd`
			echo "********Dir of log: $dir"*********** >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
			#rm -v *${REMOVE_DAYS_ago1}* *${REMOVE_DAYS_ago2}* >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
			find .  -not -name "lc.log" -not -name "channel.log" -type f -mtime +$days -print  -exec rm -rf {} \;   >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
			echo " " >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
                fi
        done
done
######################################## GZIP  FILE ###################################
logdate=$(date --date '1 days ago' '+%Y-%m-%d')
cd /home/work/local/tomcat-appupgrade/logs
gzip localhost_access_log.$logdate
find /home/work/logs  -type f -mtime +30 -print  -exec rm -rf {} \;  >> /home/work/opbin/crontab/logs/REMOE_INFORMATION.log
