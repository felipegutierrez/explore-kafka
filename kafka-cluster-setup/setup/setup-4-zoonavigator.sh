#!/bin/bash

cd explore-kafka/kafka-cluster-setup/tools/
vi zoonavigator-docker-compose.yml
# Make sure port 8001 is opened on the instance security group

# copy the zookeeper/zoonavigator-docker-compose.yml file
# run it. Use -d to run as daemon
# sudo docker-compose -f zoonavigator-docker-compose.yml up -d
sudo docker-compose -f zoonavigator-docker-compose.yml up

# access at http://127.0.0.1:8001 with connection string: zookeeper01:2181,zookeeper02:2181,zookeeper03:2181

