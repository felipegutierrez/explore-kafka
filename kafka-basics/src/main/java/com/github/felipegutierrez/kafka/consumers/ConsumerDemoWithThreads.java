package com.github.felipegutierrez.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * https://kafka.apache.org/documentation/#consumerconfigs
 */
public class ConsumerDemoWithThreads {
    private final Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThreads.class);
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String groupId = "first-app";
    private final String topic = "first-topic";

    public ConsumerDemoWithThreads() {
        // Let us create task that is going to wait for 1 thread before it starts
        CountDownLatch latch = new CountDownLatch(1);

        logger.info("Creating the consumer runnable");
        Runnable myConsumerRunnable = new ConsumerRunnable(latch, topic, bootstrapServers, groupId);

        logger.info("Starting the thread with the consumer runnable");
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            ((ConsumerRunnable) myConsumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted: " + e);
        } finally {
            logger.info("Application is closing");
        }
    }

    private class ConsumerRunnable implements Runnable {
        private final CountDownLatch latch;
        private final KafkaConsumer<String, String> consumer;

        public ConsumerRunnable(CountDownLatch latch, String topic, String bootstrapServers, String groupId) {
            this.latch = latch;

            // create properties
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // create consumer
            this.consumer = new KafkaConsumer<String, String>(properties);

            // subscribe consumer to our topic(s)
            consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
            try {
                // poll for new data
                while (true) {
                    ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Key:" + record.key() + " Value:" + record.value() +
                                " Partition:" + record.partition() + " Offset:" + record.offset());
                    }
                }
            } catch (WakeupException we) {
                logger.info("Received shutdown signal!");
            } finally {
                consumer.close();
                // tell the main code that we are done with the consumer
                latch.countDown();
            }
        }

        public void shutdown() {
            // the wakeup method is a special method to interrupt the consumer.poll
            consumer.wakeup();
        }
    }
}
