package com.github.felipegutierrez.avro.producer;

import com.github.felipegutierrez.avro.pojo.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaAvroProducerV2 {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAvroProducerV2.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String schemaRegistryUrl = "http://127.0.0.1:8081";
    private final String topic = "customer-avro-topic";

    public KafkaAvroProducerV2() {
        disclaimer();

        Properties properties = new Properties();
        // normal producer
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "10");
        // avro part
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", schemaRegistryUrl);

        Producer<String, Customer> producer = new KafkaProducer<String, Customer>(properties);

        // copied from avro examples
        Customer customer = Customer.newBuilder()
                .setAge(34)
                .setFirstName("John")
                .setLastName("Doe")
                .setHeight(178f)
                .setWeight(75f)
                // .setAutomatedEmail(false) //
                .setEmail("john.doe@gmail.com")
                .setPhoneNumber("(123)-456-7890")
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
        logger.info("Kafka schema registry to achieve FORWARD compatibility. Consume from version V1 to validate schema evolution.");
        logger.info("FORWARD evolution: means to update the producer to V2 (this class) and consume from a consumer V1 (OLD Kafka Schema Registry).");
        logger.info("the old schema registry Consumer V1 has to have the default property set.");
        logger.info("java -jar kafka-schema-registry-avro-V1/target/kafka-schema-registry-avro-V1-1.0.jar -app 2");
        logger.info("");
    }
}
