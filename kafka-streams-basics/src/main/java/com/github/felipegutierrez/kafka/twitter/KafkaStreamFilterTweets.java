package com.github.felipegutierrez.kafka.twitter;


import com.google.gson.JsonParser;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaStreamFilterTweets {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamFilterTweets.class);
    private final JsonParser jsonParser = new JsonParser();
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String topicSource = "twitter_tweets";
    private final String topicSink = "important_tweets";
    private final int followersMin = 10000;

    public KafkaStreamFilterTweets() {
        disclaimer();

        // create properties
        Properties properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "demo-kafka-streams");
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        // create topology
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // input topic
        KStream<String, String> inputTopic = streamsBuilder.stream(topicSource);
        KStream<String, String> filterStream = inputTopic.filter((key, value) -> extractUserFollowersInTweet(value) > followersMin);
        filterStream.to(topicSink);

        // build the topology
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);

        // start our stream application
        kafkaStreams.start();
    }

    private Integer extractUserFollowersInTweet(String tweetJson) {
        try {
            return jsonParser
                    .parse(tweetJson)
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .get("followers_count")
                    .getAsInt();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    private void disclaimer() {
        logger.info("Start zookeeper: ./bin/zookeeper-server-start.sh config/zookeeper.properties");
        logger.info("Start the broker: ./bin/kafka-server-start.sh config/server.properties");
        logger.info("create the topics: ./bin/kafka-topics.sh --create --topic important_tweets --zookeeper localhost:2181 --partitions 3 --replication-factor 1");
        logger.info("./bin/kafka-topics.sh --create --topic twitter_tweets --zookeeper localhost:2181 --partitions 6 --replication-factor 1");
        logger.info("Start the kafka stream: java -jar kafka-streams-twitter/target/kafka-streams-twitter-1.0.jar -app 1");
        logger.info("Start the kafka twitter producer: java -jar kafka-twitter/target/kafka-twitter-1.0.jar -app 1 -elements \"covid|corona|covid-19|felipe|bolsonaro|simone|germany\"");
        logger.info("Consume the most popular tweets: ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic important_tweets");
        logger.info("Look for the 'followers_count' attribute on the message of the consumer and check if it is greater than " + followersMin);
        logger.info("");
    }
}
