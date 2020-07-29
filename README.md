# explore-kafka

Using [Kafka 2.5.0](http://kafka.apache.org/)


Start the zookeeper:
```
./bin/zookeeper-server-start.sh config/zookeeper.properties
```
Start the Kafka brokers
```
./bin/kafka-server-start.sh config/server.properties
```
Create the topics
```
./bin/kafka-topics.sh --create --topic twitter_tweets --zookeeper localhost:2181 --partitions 6 --replication-factor 1
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

The Application that reads tweets and publish into Kafka broker is the number 7. But other applications are available.

```
mvn clean package -DskipTests
java -jar kafka-basics/target/kafka-basics-1.0.jar -app [1|2|3|4|5|6]
java -jar kafka-twitter/target/kafka-twitter-1.0.jar -app 1 -elements "corona|covid|covid-19"
java -jar kafka-elasticsearch/target/kafka-elasticsearch-1.0.jar -app [1|2|3|4]
java -jar kafka-elasticsearch/target/kafka-streams-twitter-1.0.jar -app [1]
```
