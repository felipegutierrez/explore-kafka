#!/bin/bash

# create a topic with replication factor of 3
bin/kafka-topics.sh --zookeeper zookeeper1:2181,zookeeper2:2181,zookeeper3:2181/kafka --create --topic fourth_topic --replication-factor 3 --partitions 3

# generate 10KB of random data
base64 /dev/urandom | head -c 10000 | egrep -ao "\w" | tr -d '\n' > file10KB.txt

# in a new shell: start a continuous random producer
bin/kafka-producer-perf-test.sh --topic fourth_topic --num-records 10000 --throughput 10 --payload-file file10KB.txt --producer-props acks=1 bootstrap.servers=kafka1:9092,kafka2:9092,kafka3:9092 --payload-delimiter A

# in a new shell: start a consumer
bin/kafka-console-consumer.sh --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --topic fourth_topic

# kill one kafka server - all should be fine
# kill another kafka server -
# kill the last server

# start back the servers one by one


# ERRORS:
# PRODUCER:
# org.apache.kafka.common.errors.TimeoutException: Expiring 137 record(s) for fourth_topic-0: 30024 ms has passed since batch creation plus linger time
# [2017-05-25 10:24:23,784] WARN Error while fetching metadata with correlation id 1086 : {fourth_topic=INVALID_REPLICATION_FACTOR} (org.apache.kafka.clients.NetworkClient)
# org.apache.kafka.common.errors.UnknownTopicOrPartitionException: This server does not host this topic-partition.
# [2017-05-25 10:24:23,850] WARN Received unknown topic or partition error in produce request on partition fourth_topic-0. The topic/partition may not exist or the user may not have Describe access to it (org.apache.kafka.clients.producer.internals.Sender)
# [2017-05-25 10:24:23,914] WARN Error while fetching metadata with correlation id 1092 : {fourth_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
# org.apache.kafka.common.errors.NotLeaderForPartitionException: This server is not the leader for that topic-partition.

# CONSUMER:
# [2017-05-25 10:24:23,798] WARN Error while fetching metadata with correlation id 3431 : {fourth_topic=INVALID_REPLICATION_FACTOR} (org.apache.kafka.clients.NetworkClient)
# [2017-05-25 10:24:24,081] WARN Auto-commit of offsets {fourth_topic-0=OffsetAndMetadata{offset=3948, metadata=''}} failed for group console-consumer-25246: Offset commit failed with a retriable exception. You should retry committing offsets. (org.apache.kafka.clients.consumer.internals.ConsumerCoordinator)
# [2017-05-25 10:24:24,231] WARN Received unknown topic or partition error in fetch for partition fourth_topic-0. The topic/partition may not exist or the user may not have Describe access to it (org.apache.kafka.clients.consumer.internals.Fetcher)
