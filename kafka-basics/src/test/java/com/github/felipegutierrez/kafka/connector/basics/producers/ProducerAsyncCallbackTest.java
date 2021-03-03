package com.github.felipegutierrez.kafka.connector.basics.producers;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProducerAsyncCallbackTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Mock
    ProducerAsyncCallback producerAsyncCallbackMock = new ProducerAsyncCallback();

    @Test
    public void verifyIfProducerSendsData() {

        String topic = "first-topic";
        String message = "hello world";
        int threshold = 10;

        List<ProducerRecord<String, String>> records = new ArrayList<ProducerRecord<String, String>>();

        IntStream.rangeClosed(1, threshold).forEach(i -> {
            records.add(new ProducerRecord<String, String>(topic, message + " " + i));
        });

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        producerAsyncCallbackMock.sendData(records);

        verify(producerAsyncCallbackMock).sendData(captor.capture());

        List<ProducerRecord> actualRecordList = captor.getValue();
        assertEquals(threshold, actualRecordList.size());
        assertEquals(topic, actualRecordList.get(0).topic());
        assertEquals(message + " 1", actualRecordList.get(0).value());
        assertEquals(message + " 2", actualRecordList.get(1).value());
    }

    @Test
    public void verifyException() {
        exceptionRule.expect(IndexOutOfBoundsException.class);

        String topic = "first-topic";
        String message = "hello world";
        int threshold = 3;

        List<ProducerRecord<String, String>> records = new ArrayList<ProducerRecord<String, String>>();

        IntStream.rangeClosed(1, threshold).forEach(i -> {
            records.add(new ProducerRecord<String, String>(topic, message + " " + i));
        });

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        producerAsyncCallbackMock.sendData(records);

        verify(producerAsyncCallbackMock).sendData(captor.capture());

        List<ProducerRecord> actualRecordList = captor.getValue();

        assertEquals(threshold, actualRecordList.size());
        actualRecordList.get(threshold + 1).value();
    }
}
