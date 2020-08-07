package com.github.felipegutierrez.kafka.connector.basics.producers;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * https://kafka.apache.org/documentation/#producerconfigs
 */
public class ProducerAsyncCallback {
    private final Logger logger = LoggerFactory.getLogger(ProducerAsyncCallback.class);
    private final String bootstrapServers = "127.0.0.1:9092";

    public ProducerAsyncCallback() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i < 10; i++) {
            // create producer record
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>("first-topic", "01, hello world " + i + "!");

            // send data asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time that a record is sent successfully
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received metadata: Topic: " + recordMetadata.topic() +
                                " Partition: " + recordMetadata.partition() +
                                " Offset: " + recordMetadata.offset() +
                                " Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error on sending message: " + e.getMessage());
                    }
                }
            });
        }
        // flush data
        producer.flush();
        //  flush and close producer
        producer.close();
    }
}
