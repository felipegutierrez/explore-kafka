#!/bin/bash

# Make sure you have done the steps from 5 on all machines
# we repeat the steps from 6

# Add file limits configs - allow to open 100,000 file descriptors
echo "* hard nofile 100000
* soft nofile 100000" | sudo tee --append /etc/security/limits.conf
sudo reboot
sudo service zookeeper start
sudo chown -R ubuntu:ubuntu /data/kafka

# edit the config
rm config/server.properties
# MAKE SURE TO USE ANOTHER BROKER ID
nano config/server.properties
# launch kafka - make sure things look okay
bin/kafka-server-start.sh config/server.properties

# Install Kafka boot scripts
sudo nano /etc/init.d/kafka
sudo chmod +x /etc/init.d/kafka
sudo chown root:root /etc/init.d/kafka
# you can safely ignore the warning
sudo update-rc.d kafka defaults

# start kafka
sudo service kafka start
# verify it's working
nc -vz localhost 9092
# look at the logs
cat /home/ubuntu/kafka/logs/server.log
# make sure to fix the __consumer_offsets topic
bin/kafka-topics.sh --zookeeper zookeeper1:2181/kafka --config min.insync.replicas=1 --topic __consumer_offsets --alter

# read the topic on broker 1 by connecting to broker 2!
bin/kafka-console-consumer.sh --bootstrap-server kafka2:9092 --topic first_topic --from-beginning


# DO THE SAME FOR BROKER 3

# After, you should see three brokers here
bin/zookeeper-shell.sh localhost:2181
ls /kafka/brokers/ids

# you can also check the zoonavigator UI
