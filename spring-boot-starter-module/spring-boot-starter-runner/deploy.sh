#!/bin/sh
APP_NAME="module-matchmaker-3.0.0-exec.jar"
JAVA_OPTS="-server -Xmx2g -XX:PermSize=96m -XX:MaxPermSize=256m -Xmn1024m  -XX:+UseConcMarkSweepGC -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"
echo "====== $APP_NAME ======"
pid=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{ print $2 }')
if [ $pid ]; then
    echo "=== begin kill java process, pid is:$pid"
    kill -15 $pid
else
    echo "=== process $pid not exists or stop success"
fi

while true;do
    count=`ps -ef|grep $pid|grep -v grep`
    if [ "$?" != "0" ];then
        echo ">>> $pid is stoped!"
        break
    else
        echo ">>> $pid is running..."
    fi
    sleep 1
done

nohup java $JAVA_OPTS -jar $APP_NAME  >app.log 2>&1 &

sleep 2

spid=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{ print $2 }')
if [ $spid ];then
  echo "start success ..."
  echo "pid is: $spid"
else
  echo "start fail ..."
fi
exit 0
