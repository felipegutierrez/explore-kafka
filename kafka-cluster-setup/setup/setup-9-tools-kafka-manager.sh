#!/bin/bash

# make sure you open port 9000 on the security group

# make sure you can access the zookeeper endpoints
nc -vz zookeeper01 2181
nc -vz zookeeper02 2181
nc -vz zookeeper03 2181

# make sure you can access the kafka endpoints
nc -vz kafka01 9092
nc -vz kafka02 9092
nc -vz kafka03 9092

# copy the kafka-manager-docker-compose.yml file
cd explore-kafka/kafka-cluster-setup/tools
vi kafka-manager-docker-compose.yml

# launch it
# docker-compose -f kafka-manager-docker-compose.yml up -d
sudo docker-compose -f kafka-manager-docker-compose.yml up

# access it fro mthe web browser: http://127.0.0.1:9000/
