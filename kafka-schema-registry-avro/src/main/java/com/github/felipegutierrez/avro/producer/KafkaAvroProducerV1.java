package com.github.felipegutierrez.avro.producer;

import com.github.felipegutierrez.avro.pojo.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaAvroProducerV1 {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAvroProducerV1.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String schemaRegistryUrl = "http://127.0.0.1:8081";
    private final String topic = "customer-avro";

    public KafkaAvroProducerV1() {
        disclaimer();

        Properties properties = new Properties();
        // normal producer
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", schemaRegistryUrl);

        Producer<String, Customer> producer = new KafkaProducer<String, Customer>(properties);

        // copied from avro examples
        Customer customer = Customer.newBuilder()
                .setAge(34)
                .setAutomatedEmail(false)
                .setFirstName("John")
                .setLastName("Doe")
                .setHeight(178f)
                .setWeight(75f)
                .build();

        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(
                topic, customer
        );

        logger.info(customer.toString());
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    logger.info(metadata.toString());
                } else {
                    logger.error("ERROR: ", exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });

        producer.flush();
        producer.close();
    }

    private void disclaimer() {
        logger.info("Please start the docker Kafka Schema registry form confluent before to use this class");
        logger.info("sudo docker-compose up");
        logger.info("After executing this class check the new schema and topic created or use the docker compose Kafka CLI");
        logger.info("sudo docker run -it --rm --net=host confluentinc/cp-schema-registry:3.3.1 bash");
        logger.info("kafka-avro-console-consumer --topic test-avro --bootstrap-server 127.0.0.1:9092 --property schema.registry.url=http://127.0.0.1:8081 --from-beginning");
    }
}
