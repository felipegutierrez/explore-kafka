package com.github.felipegutierrez.kafka.basics.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerDemoTest {
    private final String topic = "first-topic";
    // @Mock
    // @Spy
    // ConsumerDemo consumerDemoMock = new ConsumerDemo();
    private ConsumerDemo consumerDemo;
    private MockConsumer mockConsumer;

    @Before
    public void setUp() {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoTest.class.getName());

        mockConsumer = new MockConsumer(OffsetResetStrategy.EARLIEST);

        consumerDemo = new ConsumerDemo();
        consumerDemo.setConsumer(mockConsumer);

        // mockConsumer.subscribe(Arrays.asList(topic));
        mockConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));

        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(new TopicPartition(topic, 0), 0L);
        mockConsumer.updateBeginningOffsets(beginningOffsets);
    }

    @Test
    public void testConsumer() throws InterruptedException {
        mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 0L, "mykey", "myvalue0"));
        mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 1L, "mykey", "myvalue1"));
        mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 2L, "mykey", "myvalue2"));
        mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 3L, "mykey", "myvalue3"));
        mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 4L, "mykey", "myvalue4"));

        CompletableFuture.runAsync(() -> {
            consumerDemo.startConsuming();
        });

        Thread.sleep(3000);

        consumerDemo.stopConsuming();

        // assertThat(memoryAppender.countEventsForLogger(ConsumerDemoTest.class.getName())).isEqualTo(5);
    }
}
