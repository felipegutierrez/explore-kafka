package com.github.felipegutierrez.kafka.registry.avro.consumer;

import com.github.felipegutierrez.kafka.registry.avro.pojo.Customer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class KafkaAvroConsumerV1 {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAvroConsumerV1.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String groupId = "customer-consumer-group-v1";
    private final String schemaRegistryUrl = "http://127.0.0.1:8081";
    private final String topic = "customer-avro-topic";

    public KafkaAvroConsumerV1() {
        this("earliest");
    }

    public KafkaAvroConsumerV1(String offset) {
        disclaimer();

        Properties properties = new Properties();
        // normal consumer
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);
        // avro part (deserializer)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.setProperty("schema.registry.url", schemaRegistryUrl);
        properties.setProperty("specific.avro.reader", "true");

        KafkaConsumer<String, Customer> kafkaConsumer = new KafkaConsumer<String, Customer>(properties);
        kafkaConsumer.subscribe(Collections.singleton(topic));

        logger.info("Waiting for data...");

        while (true) {
            try {
                logger.info("Polling");
                ConsumerRecords<String, Customer> records = kafkaConsumer.poll(1000);

                for (ConsumerRecord<String, Customer> record : records) {
                    Customer customer = record.value();
                    logger.info(customer.toString());
                }
                logger.info("commit the offsets so when we restart the consumer we resume the topic from where we stopped.");
                kafkaConsumer.commitSync();
            } catch (SerializationException se) {
                se.printStackTrace();
            }
        }
    }

    private void disclaimer() {
        logger.info("Please start the docker Kafka Schema registry form confluent before to use this class");
        logger.info("sudo docker-compose up");
        logger.info("After executing this class check the new schema and topic created or use the docker compose Kafka CLI");
        logger.info("sudo docker run -it --rm --net=host confluentinc/cp-schema-registry:3.3.1 bash");
        logger.info("kafka-avro-console-consumer --topic test-avro --bootstrap-server 127.0.0.1:9092 --property schema.registry.url=http://127.0.0.1:8081 --from-beginning");
    }
}
