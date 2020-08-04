#!/bin/bash

echo "start the docker-compose with Kafka"
echo "sudo docker-compose up"

echo "create the topics"
kafka-topics --create --zookeeper localhost:2181 --topic rest-binary --replication-factor 1 --partitions 1
kafka-topics --create --zookeeper localhost:2181 --topic rest-json --replication-factor 1 --partitions 1
kafka-topics --create --zookeeper localhost:2181 --topic rest-avro --replication-factor 1 --partitions 1

echo "keep consuming from CLI"
echo "kafka-avro-console-consumer --topic rest-avro --bootstrap-server 127.0.0.1:9092 --property schema.registry.url=http://127.0.0.1:8081 --from-beginning"

