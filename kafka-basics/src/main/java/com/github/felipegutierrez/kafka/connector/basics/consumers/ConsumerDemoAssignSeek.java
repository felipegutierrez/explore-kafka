package com.github.felipegutierrez.kafka.connector.basics.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * https://kafka.apache.org/documentation/#consumerconfigs
 */
public class ConsumerDemoAssignSeek {
    private final Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String topic = "first-topic";

    public ConsumerDemoAssignSeek() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // assign and seek are mostly used to replay data or fetch a specific message
        // assign
        TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
        consumer.assign(Arrays.asList(partitionToReadFrom));

        // seek
        long offsetToReadFrom = 10L;
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        int numberMessagesToRead = 5;
        boolean keepOnReading = true;
        int numberMessagesCount = 0;
        // poll for new data
        while (keepOnReading) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                numberMessagesCount++;
                logger.info("Key:" + record.key() + " Value:" + record.value() +
                        " Partition:" + record.partition() + " Offset:" + record.offset());
                if (numberMessagesCount >= numberMessagesToRead) {
                    keepOnReading = false;
                    break;
                }
            }
        }
        logger.info("Exiting the application");
    }
}
