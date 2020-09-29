package com.github.felipegutierrez.kafka.stream.bankbalance

import java.time.Instant
import java.util.Properties

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{Serdes, StringSerializer}
import org.apache.kafka.connect.json.{JsonDeserializer, JsonSerializer}
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream}
import org.slf4j.{Logger, LoggerFactory}

object BankBalanceExactlyOnceScala {
  val topic: String = "bank-transaction";
  val bootstrapServers: String = "127.0.0.1:9092"

  def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(BankBalanceExactlyOnceScala.getClass)

    // create properties
    val config: Properties = new Properties
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    // producer acks
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.RETRIES_CONFIG, "3");
    config.put(ProducerConfig.LINGER_MS_CONFIG, "1");
    // leverage idempotent
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")

    // json Serde
    val jsonSerializer = new JsonSerializer
    val jsonDeserializer = new JsonDeserializer
    val jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer)

    val builder = new StreamsBuilder
    val bankTransactions: KStream[String, JsonNode] = builder
      .stream("bank-transactions")(Consumed.`with`(Serdes.String, jsonSerde))

    // create the initial json object for balances
    val initialBalance = JsonNodeFactory.instance.objectNode
    initialBalance.put("count", 0)
    initialBalance.put("balance", 0)
    initialBalance.put("time", Instant.ofEpochMilli(0L).toString)

    implicit val materializer = Materialized.`with`(Serdes.String, jsonSerde)
    // val matererlized: Materialized[String, JsonNode, KeyValueStore[Bytes, Array[Byte]]] =
    // Materialized.as("bank-balance-agg")(Consumed.`with`(Serdes.String, jsonSerde))

    //     val bankBalance: KTable[String, JsonNode] =
    bankTransactions
      .peek((key: String, value: JsonNode) => logger.info("bankTransactions - key: " + key + " amount: " + value.get("amount").asInt))
      .groupByKey(Serdes[String])
    // .aggregate(() => initialBalance)(key: String, transaction: JsonNode, balance: JsonNode) => aggregateNewBalance(transaction, balance), Materialized.as[String, JsonNode, KeyValueStore[Bytes, Array[Byte]]]("bank-balance-agg").withKeySerde(Serdes.String).withValueSerde(jsonSerde))

    // bankBalance.toStream.peek((key: String, value: JsonNode) => System.out.println("bankBalance      - key: " + key + " balance: " + value.get("balance").asInt)).to("bank-balance-exactly-once", Produced.`with`(Serdes.String, jsonSerde))

    val streams = new KafkaStreams(builder.build, config)
    // only do this in dev - not in prod
    streams.cleanUp()
    streams.start()

    // print the topology
    streams.localThreadsMetadata().forEach(t => println(t.toString))

    // shutdown hook to correctly close the streams application
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        streams.close()
      }
    })
  }

  private def aggregateNewBalance(transaction: JsonNode, balance: JsonNode): JsonNode = {
    // create a new balance json object
    val newBalance = JsonNodeFactory.instance.objectNode
    newBalance.put("count", balance.get("count").asInt + 1)
    newBalance.put("balance", balance.get("balance").asInt + transaction.get("amount").asInt)
    val balanceEpoch = Instant.parse(balance.get("time").asText).toEpochMilli
    val transactionEpoch = Instant.parse(transaction.get("time").asText).toEpochMilli
    val newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch))
    newBalance.put("time", newBalanceInstant.toString)
    newBalance
  }
}
