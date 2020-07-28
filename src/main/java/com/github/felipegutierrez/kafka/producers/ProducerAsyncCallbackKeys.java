package com.github.felipegutierrez.kafka.producers;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * https://kafka.apache.org/documentation/#producerconfigs
 */
public class ProducerAsyncCallbackKeys {
    private final Logger logger = LoggerFactory.getLogger(ProducerAsyncCallbackKeys.class);
    private final String bootstrapServers = "127.0.0.1:9092";

    public ProducerAsyncCallbackKeys() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i < 10; i++) {
            String topic = "first-topic";
            String key = "id_" + i;
            String value = "hello world " + i + "!";

            logger.info("key: " + key + " topic: " + topic + " value: " + value);
            // create producer record
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);

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
            });// .get(); // this is NOT asynchronous!
        }
        // flush data
        producer.flush();
        //  flush and close producer
        producer.close();
    }
}
