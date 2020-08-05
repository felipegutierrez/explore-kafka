#!/bin/bash
# Packages
sudo apt-get update && \
      sudo apt-get -y install wget ca-certificates zip net-tools vim nano tar netcat

# Java Open JDK 8
sudo apt-get -y install openjdk-8-jdk
java -version

# Disable RAM Swap - can set to 0 on certain Linux distro
sudo sysctl vm.swappiness=1
echo 'vm.swappiness=1' | sudo tee --append /etc/sysctl.conf

# Add hosts entries (mocking DNS) - put relevant IPs at /etc/hosts
#127.0.0.1	localhost cow-11 kafka00 zookeeper00
#192.168.56.1    cow-11 kafka00 zookeeper00
## Virtual Machines
#192.168.56.101  worker01 kafka01 zookeeper01
#192.168.56.102  worker02 kafka02 zookeeper02
#192.168.56.103  worker03 kafka03 zookeeper03

# download Zookeeper and Kafka. Recommended is latest Kafka (0.10.2.1) and Scala 2.12
wget https://mirrors.ae-online.de/apache/kafka/2.5.0/kafka_2.12-2.5.0.tgz
tar -xvzf kafka_2.12-2.5.0.tgz
rm kafka_2.12-2.5.0.tgz
mv kafka_2.12-2.5.0 kafka
cd kafka/
# Zookeeper quickstart
cat config/zookeeper.properties
bin/zookeeper-server-start.sh config/zookeeper.properties
# binding to port 2181 -> you're good. Ctrl+C to exit

# Testing Zookeeper install
# Start Zookeeper in the background
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
bin/zookeeper-shell.sh localhost:2181
ls /
# demonstrate the use of a 4 letter word
echo "ruok" | nc localhost 2181 ; echo

# Install Zookeeper boot scripts
sudo cp /home/felipe/workspace-idea/explore-kafka/kafka-cluster-setup/zookeeper/zookeeper /etc/init.d/zookeeper
# change the respectives direcotries
sudo vi /etc/init.d/zookeeper
sudo chmod +x /etc/init.d/zookeeper
sudo chown root:root /etc/init.d/zookeeper
# you can safely ignore the warning
sudo update-rc.d zookeeper defaults
# stop zookeeper
sudo service zookeeper stop
# verify it's stopped
nc -vz localhost 2181
# start zookeeper
sudo service zookeeper start
# verify it's started
nc -vz localhost 2181
echo "ruok" | nc localhost 2181 ; echo
# check the logs
cat logs/zookeeper.out
