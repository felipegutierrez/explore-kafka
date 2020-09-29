package com.github.felipegutierrez.kafka.stream.colour;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Properties;

/**
 * <pre>
 * Start Zookeeper:
 * ./bin/zookeeper-server-start.sh config/zookeeper.properties
 * Start Kafka broker
 * ./bin/kafka-server-start.sh config/server.properties
 * Create topics:
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic user-keys-and-colours --partitions 1 --replication-factor 1
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic favourite-colour-input --partitions 1 --replication-factor 1
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic favourite-colour-output --partitions 1 --replication-factor 1
 * Launch this application, the consumer, and the producer
 * ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic favourite-colour-output --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
 * ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic favourite-colour-input
 * felipe,black
 * simone,brown
 * telma,brown
 * joao,black
 * simone,black
 * simone,pink
 * joao,black
 * simone,blue
 * felipe,black
 * simone,pink
 * renata,pink
 * felipe,black
 * simone,blue
 * felipe,black
 * renata,black
 * simone,black
 * </pre>
 */
public class FavouriteColourApp {

    public FavouriteColourApp() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-java");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
        // using exactly-once in Kafka Streams
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        StreamsBuilder builder = new StreamsBuilder();
        // Step 1: We create the topic of users keys to colours
        KStream<String, String> textLines = builder.stream("favourite-colour-input");

        KStream<String, String> usersAndColours = textLines
                // 1 - we ensure that a comma is here as we will split on it
                .filter((key, value) -> value.contains(","))
                // 2 - we select a key that will be the user id (lowercase for safety)
                .selectKey((key, value) -> value.split(",")[0].toLowerCase())
                // 3 - we get the colour from the value (lowercase for safety)
                .mapValues(value -> value.split(",")[1].toLowerCase())
                // 4 - we filter undesired colours (could be a data sanitization step
                //.filter((user, colour) -> Arrays.asList("green", "blue", "red").contains(colour));
                .peek((user, colour) -> System.out.println("user[" + user + "] colour[" + colour + "]"));

        // usersAndColours.to("favourite-colour-output");
        usersAndColours.to("user-keys-and-colours");
        Serde<String> stringSerde = Serdes.String();
        Serde<Long> longSerde = Serdes.Long();

        // step 2 - we read that topic as a KTable so that updates are read correctly
        KTable<String, String> usersAndColoursTable = builder.table("user-keys-and-colours");
        // step 3 - we count the occurrences of colours
        KTable<String, Long> favouriteColours = usersAndColoursTable
                // 5 - we group by colour within the KTable
                .groupBy((user, colour) -> new KeyValue<>(colour, colour))
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("CountsByColours").withKeySerde(stringSerde).withValueSerde(longSerde));
        // 6 - we output the results to a Kafka Topic - don't forget the serializers
        favouriteColours
                .toStream()
                .peek((colour, count) -> System.out.println("colour = " + colour + ", count = " + count))
                .to("favourite-colour-output", Produced.with(stringSerde, longSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        // only do this in dev - not in prod
        streams.cleanUp();
        streams.start();

        // print the topology
        streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static void main(String[] args) {
        new FavouriteColourApp();
    }
}
