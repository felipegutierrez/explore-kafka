package com.github.felipegutierrez.kafka.stream.bankbalance;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BankTransactionsProducerTest {

    @Test
    public void newRandomTransactionsTest() {
        String keyTotest = "john";
        ProducerRecord<String, String> record = BankTransactionsProducer.newRandomTransaction(keyTotest);
        String key = record.key();

        assertEquals(key, keyTotest);

        String value = record.value();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        try {
            JsonParser parser = factory.createParser(value);
            JsonNode actualObj = mapper.readTree(parser);
            assertNotNull(actualObj);

            JsonNode jsonNodeName = actualObj.get("name");
            assertEquals(jsonNodeName.asText(), keyTotest);

            JsonNode jsonNodeAmount = actualObj.get("amount");
            assertTrue("Error, amount is too low", jsonNodeAmount.asInt() >= 0);
            assertTrue("Error, amount is too high", jsonNodeAmount.asInt() <= 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
