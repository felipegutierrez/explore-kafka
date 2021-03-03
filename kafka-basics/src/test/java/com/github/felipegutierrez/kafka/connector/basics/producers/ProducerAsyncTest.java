package com.github.felipegutierrez.kafka.connector.basics.producers;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProducerAsyncTest {

    @Mock
    ProducerAsync producerAsyncMock = new ProducerAsync();

    @Test
    public void verifyIfProducerSendsData() {

        String topic = "first-topic";
        String value = "01, hello world!";

        ArgumentCaptor<ProducerRecord> captor = ArgumentCaptor.forClass(ProducerRecord.class);

        producerAsyncMock.sendData(new ProducerRecord<String, String>(topic, value));

        verify(producerAsyncMock).sendData(captor.capture());

        ProducerRecord actualRecord = captor.getValue();
        assertEquals(topic, actualRecord.topic());
        assertEquals(value, actualRecord.value());
    }
}
