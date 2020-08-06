#!/bin/bash

# we can create topics with replication-factor 3 now!
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --create --topic second_topic --replication-factor 3 --partitions 3

# we can publish data to Kafka using the bootstrap server list!
bin/kafka-console-producer.sh --broker-list kafka01:9092,kafka02:9092,kafka03:9092 --topic second_topic

# we can read data using any broker too!
bin/kafka-console-consumer.sh --bootstrap-server kafka01:9092,kafka02:9092,kafka03:9092 --topic second_topic --from-beginning

# we can create topics with replication-factor 3 now!
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --create --topic third_topic --replication-factor 3 --partitions 3

# let's list topics
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --list

# let's describe the topic
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --topic second_topic --describe

# publish some data
bin/kafka-console-producer.sh --broker-list kafka01:9092,kafka02:9092,kafka03:9092 --topic third_topic

# let's delete that topic
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --delete --topic third_topic

# it should be deleted shortly:
bin/kafka-topics.sh --zookeeper zookeeper01:2181,zookeeper02:2181,zookeeper03:2181/kafka --list
