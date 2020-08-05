# explore-kafka

Using [Kafka 2.5.0](http://kafka.apache.org/) to implement and practice the exercises from the Apache Kafka Series:

 - [Learn Apache Kafka for Beginners v2](https://www.udemy.com/course/kafka-connect/)
 - [Kafka Connect Hands-on Learning](https://www.udemy.com/course/kafka-connect/)
 - [Confluent Schema Registry & REST Proxy](https://www.udemy.com/course/confluent-schema-registry/)
 - [Kafka Cluster Setup & Administration](https://www.udemy.com/course/kafka-cluster-setup/)


Start the zookeeper:
```
./bin/zookeeper-server-start.sh config/zookeeper.properties
```
Start the Kafka brokers
```
./bin/kafka-server-start.sh config/server.properties
```
Topics
```
./bin/kafka-topics.sh  --zookeeper localhost:2181 --list
./bin/kafka-topics.sh  --zookeeper localhost:2181 --create   --topic twitter_tweets --partitions 6 --replication-factor 1
./bin/kafka-topics.sh  --zookeeper localhost:2181 --describe --topic twitter_tweets
# Add, describe, delete configuration for a topic
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name twitter_tweets --describe
# min.insync.replicas=2 means that 2 nodes besides the leader has to synchronize the messages.
# However, because our --replication-factor=1 (because I tested on my local machine) the min.insync.replicas=2 has no efect
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name twitter_tweets --add-config min.insync.replicas=2  --alter
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name twitter_tweets --delete-config min.insync.replicas --alter
```
Log cleanup policies
```
./bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic __consumer_offsets
```
Start the producer from the command line or the Java producer Kafka application
```
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic first-topic --property parse.key=true --property key.separator=,
```
Start the consumer with or without group and key-value properties
```
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first-topic
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first-topic --group my-first-app --property print.key=true --property key.separator=,
```

The Application that reads tweets and publish into Kafka broker is the `kafka-twitter-*.jar`, but other applications are available.

```
mvn clean package
java -jar kafka-basics/target/kafka-basics-1.0.jar -app [1|2|3|4|5|6]
java -jar kafka-twitter/target/kafka-twitter-1.0.jar -app 1 -elements "corona|covid|covid-19"
java -jar kafka-elasticsearch/target/kafka-elasticsearch-1.0.jar -app [1|2|3|4]
java -jar kafka-streams-twitter/target/kafka-streams-twitter-1.0.jar -app [1]
java -jar kafka-source-connector-github/target/kafka-source-connector-github-1.0.jar -app [1]
java -jar avro-examples/target/avro-examples-1.0.jar -app [1|2|3|4]
java -jar kafka-schema-registry-avro-V1/target/kafka-schema-registry-avro-V1-1.0.jar -app [1|2]
java -jar kafka-schema-registry-avro-V2/target/kafka-schema-registry-avro-V2-1.0.jar -app [1|2]
```


