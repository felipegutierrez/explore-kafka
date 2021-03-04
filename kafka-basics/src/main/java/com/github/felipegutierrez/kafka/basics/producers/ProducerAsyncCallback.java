package com.github.felipegutierrez.kafka.basics.producers;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * https://kafka.apache.org/documentation/#producerconfigs
 */
public class ProducerAsyncCallback {
    private final Logger logger = LoggerFactory.getLogger(ProducerAsyncCallback.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final KafkaProducer<String, String> producer;
    private final Callback producerCallback;

    public ProducerAsyncCallback() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<String, String>(properties);

        this.producerCallback = new Callback() {
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
        };
    }

    public void sendData(List<ProducerRecord<String, String>> records) {
        records.stream().forEach(producerRecord -> {
            // send data asynchronous
            producer.send(producerRecord, producerCallback);
        });
        // flush data
        producer.flush();
    }

    public void closeProducer() {
        //  flush and close producer
        producer.close();
    }
}
