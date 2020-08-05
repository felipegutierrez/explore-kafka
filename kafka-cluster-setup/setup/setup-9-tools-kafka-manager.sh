#!/bin/bash

# make sure you open port 9000 on the security group

# make sure you can access the zookeeper endpoints
nc -vz zookeeper1 2181
nc -vz zookeeper2 2181
nc -vz zookeeper3 2181

# make sure you can access the kafka endpoints
nc -vz kafka1 9092
nc -vz kafka2 9092
nc -vz kafka3 9092

# copy the kafka-manager-docker-compose.yml file
nano kafka-manager-docker-compose.yml

# launch it
docker-compose -f kafka-manager-docker-compose.yml up -d
