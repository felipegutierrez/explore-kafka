package com.github.felipegutierrez.kafka.stream.bankbalance;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class BankTransactionsProducer {

    private static final String topic = "bank-transaction";
    private final Logger logger = LoggerFactory.getLogger(BankTransactionsProducer.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private Boolean running;

    public BankTransactionsProducer() {
        this.running = true;

        // create properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // producer acks
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        // leverage idempotent
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        int i = 0;
        while (running) {
            logger.info("Producing batch: " + i);
            try {
                producer.send(newRandomTransaction("felipe"));
                Thread.sleep(100);
                producer.send(newRandomTransaction("simone"));
                Thread.sleep(100);
                producer.send(newRandomTransaction("john"));
                Thread.sleep(100);
                producer.send(newRandomTransaction("bob"));
                Thread.sleep(100);
                i++;
            } catch (InterruptedException ie) {
                this.running = false;
                break;
            }
        }
        producer.close();
    }

    public static void main(String[] args) {
        new BankTransactionsProducer();
    }

    public static ProducerRecord<String, String> newRandomTransaction(String name) {
        ObjectNode transaction = JsonNodeFactory.instance.objectNode();
        Integer amount = ThreadLocalRandom.current().nextInt(0, 100);
        Instant now = Instant.now();
        transaction.put("name", name);
        transaction.put("amount", amount);
        transaction.put("time", now.toString());
        return new ProducerRecord<>(topic, name, transaction.toString());
    }
}
