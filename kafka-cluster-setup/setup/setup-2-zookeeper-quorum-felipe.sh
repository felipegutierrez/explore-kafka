#!/bin/bash
# create data dictionary for zookeeper
sudo mkdir -p /data/zookeeper
sudo chown -R flink:flink /data/
# declare the server's identity at zookeeper01, zookeeper02, and zookeeper03
echo "1" > /data/zookeeper/myid
echo "2" > /data/zookeeper/myid
echo "3" > /data/zookeeper/myid
# edit the zookeeper settings
mv /home/flink/kafka/config/zookeeper.properties /home/flink/kafka/config/zookeeper.properties.bkp
cp kafka-cluster-setup/zookeeper/zookeeper.properties /home/flink/kafka/config/zookeeper.properties
vi /home/flink/kafka/config/zookeeper.properties
# restart the zookeeper service
sudo service zookeeper stop
sudo service zookeeper start
# observe the logs - need to do this on every machine
cat /home/flink/kafka/logs/zookeeper.out | head -100
nc -vz localhost 2181
nc -vz localhost 2888
nc -vz localhost 3888
echo "ruok" | nc localhost 2181 ; echo
echo "stat" | nc localhost 2181 ; echo
bin/zookeeper-shell.sh localhost:2181
# not happy
ls /
