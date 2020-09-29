package com.github.felipegutierrez.kafka.stream.bankbalance

import java.time.Instant
import java.util.Properties
import java.util.concurrent.ThreadLocalRandom

import com.fasterxml.jackson.databind.node.{JsonNodeFactory, ObjectNode}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}

object BankTransactionsProducerScala {

  val topic: String = "bank-transaction";

  def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(BankTransactionsProducerScala.getClass)

    val bootstrapServers: String = "127.0.0.1:9092"
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
    // create producer
    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](config)

    var running: Boolean = true
    var i: Int = 0
    while (running) {
      logger.info("Producing batch: " + i)
      try {
        producer.send(newRandomTransaction("felipe"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("simone"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("john"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("bob"))
        Thread.sleep(100)
        i += 1
      } catch {
        case ie: InterruptedException => running = false
      }
    }
    producer.close()
  }

  def newRandomTransaction(name: String): ProducerRecord[String, String] = {
    val transaction: ObjectNode = JsonNodeFactory.instance.objectNode
    val amount: Integer = ThreadLocalRandom.current.nextInt(0, 100)
    val now: Instant = Instant.now
    transaction.put("name", name)
    transaction.put("amount", amount)
    transaction.put("time", now.toString)
    new ProducerRecord(topic, name, transaction.toString)
  }
}
