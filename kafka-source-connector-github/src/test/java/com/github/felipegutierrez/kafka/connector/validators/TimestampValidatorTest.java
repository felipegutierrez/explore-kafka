package com.github.felipegutierrez.kafka.connector.validators;

import org.apache.kafka.common.KafkaException;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TimestampValidatorTest {

    TimestampValidator timestampValidator = new TimestampValidator();

    @Test
    public void ensureValid() {
        timestampValidator.ensureValid("CONFIG_TIME", "2020-08-28T10:05:15Z");
    }

    @Test
    public void ensureValidEmptyDate() {
        try {
            timestampValidator.ensureValid("CONFIG_TIME", "");
            fail("It should have a KafkaException here.");
        } catch (Exception ex) {
            if (!(ex instanceof KafkaException)) {
                fail("There is an exception but it is not a KafkaException.");
            }
        }
    }

    @Test
    public void ensureValidWrongDate() {
        try {
            timestampValidator.ensureValid("CONFIG_TIME", "2020-08-28");
            fail("It should have a KafkaException here.");
        } catch (Exception ex) {
            if (!(ex instanceof KafkaException)) {
                fail("There is an exception but it is not a KafkaException.");
            }
        }
    }
}