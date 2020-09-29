package com.github.felipegutierrez.kafka.stream.colour

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

/**
 * <pre>
 * Start Zookeeper:
 * ./bin/zookeeper-server-start.sh config/zookeeper.properties
 * Start Kafka broker
 * ./bin/kafka-server-start.sh config/server.properties
 * Create the topics:
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic favourite-colour-input --partitions 1 --replication-factor 1
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic user-keys-and-colours-scala --partitions 1 --replication-factor 1
 * ./bin/kafka-topics.sh  --zookeeper localhost:2181 --create --topic favourite-colour-output-scala --partitions 1 --replication-factor 1
 * Launch the consumer:
 * ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic favourite-colour-output-scala --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
 * Launch the producer:
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
object FavouriteColourAppScala {
  def main(args: Array[String]): Unit = {
    val config: Properties = new Properties
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-scala")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
    // using exactly-once in Kafka Streams
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE)

    val builder: StreamsBuilder = new StreamsBuilder

    // Step 1: We create the topic of users keys to colours
    val textLines: KStream[String, String] = builder.stream[String, String]("favourite-colour-input")

    val usersAndColours: KStream[String, String] = textLines
      // 1 - we ensure that a comma is here as we will split on it
      .filter((key: String, value: String) => value.contains(","))
      // 2 - we select a key that will be the user id (lowercase for safety)
      .selectKey[String]((key: String, value: String) => value.split(",")(0).toLowerCase)
      // 3 - we get the colour from the value (lowercase for safety)
      .mapValues((value: String) => value.split(",")(1).toLowerCase)
      // 4 - we filter undesired colours (could be a data sanitization step)
      // .filter((user: String, colour: String) => List("green", "blue", "red").contains(colour))
      .peek((user: String, colour: String) => println("user[" + user + "] colour[" + colour + "]"))

    val intermediaryTopic = "user-keys-and-colours-scala"
    usersAndColours.to(intermediaryTopic)

    // step 2 - we read that topic as a KTable so that updates are read correctly
    val usersAndColoursTable: KTable[String, String] = builder.table(intermediaryTopic)

    val stringSerde: Serde[String] = Serdes.String
    val longSerde: Serde[Long] = Serdes.Long

    // step 3 - we count the occurrences of colours
    val favouriteColours: KTable[String, Long] = usersAndColoursTable
      // 5 - we group by colour within the KTable
      .groupBy((user: String, colour: String) => (colour, colour))
      .count()

    // 6 - we output the results to a Kafka Topic - don't forget the serializers
    favouriteColours
      .toStream
      .peek((colour: String, count: Long) => println("colour = " + colour + ", count = " + count))
      .to("favourite-colour-output-scala")(Produced.`with`(stringSerde, longSerde))

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    streams.cleanUp()
    streams.start()

    // print the topology
    streams.localThreadsMetadata().forEach(t => System.out.print(t.toString))

    // shutdown hook to correctly close the streams application
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        streams.close()
      }
    })
  }
}
