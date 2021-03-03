package com.github.felipegutierrez.kafka.util;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RecordUtils {

    /**
     * Create a List of producer records
     *
     * @param topic
     * @param message
     * @param threshold
     * @return
     */
    public static List<ProducerRecord<String, String>> getProducerRecordList(String topic, String message, int threshold) {
        List<ProducerRecord<String, String>> records = new ArrayList<ProducerRecord<String, String>>();

        IntStream.rangeClosed(1, threshold)
                .forEach(i -> {
                    records.add(new ProducerRecord<String, String>(topic, message + " " + i));
                });
        return records;
    }
}
