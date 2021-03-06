package com.github.felipegutierrez.kafka.basics.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * https://kafka.apache.org/documentation/#producerconfigs
 */
public class ProducerAsync {
    private final String bootstrapServers = "127.0.0.1:9092";
    private final KafkaProducer<String, String> producer;

    public ProducerAsync() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        this.producer = new KafkaProducer<String, String>(properties);
    }

    public void sendData(ProducerRecord<String, String> record) {
        // create producer record

        // send data asynchronous
        producer.send(record);

        // flush data
        producer.flush();
        //  flush and close producer
        producer.close();
    }
}
