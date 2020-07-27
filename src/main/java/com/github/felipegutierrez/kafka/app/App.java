package com.github.felipegutierrez.kafka.app;

import com.github.felipegutierrez.kafka.producers.ProducerStringAsync;

public class App {
    public static void main(String[] args) {
        System.out.printf("Exploring Kafka");
        new ProducerStringAsync();
    }
}
