package com.github.felipegutierrez.kafka.basics.consumers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

/**
 * https://kafka.apache.org/documentation/#consumerconfigs
 */
public class ConsumerDemo {
    private final Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String groupId = "first-app";
    private Consumer<String, String> consumer;
    private Boolean running = false;

    public ConsumerDemo() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        this.consumer = new KafkaConsumer<String, String>(properties);
    }

    public Consumer<String, String> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void subscribe() {
        subscribe("first-topic");
    }

    public void subscribe(String topic) {
        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));
    }

    public void startConsuming() {
        try {
            running = true;
            // poll for new data
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                StreamSupport.stream(records.spliterator(), false)
                        .map(record -> "Key:" + record.key() + " Value:" + record.value() + " Partition:" + record.partition() + " Offset:" + record.offset())
                        .forEach(message -> logger.info(message));
                this.consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.consumer.close();
        }
    }

    public void stopConsuming() {
        running = false;
    }
}
