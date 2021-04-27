package com.github.felipegutierrez.kafka.connector.validators;

import junit.framework.TestCase;
import org.apache.kafka.common.KafkaException;

public class BatchSizeValidatorTest extends TestCase {

    BatchSizeValidator batchSizeValidator = new BatchSizeValidator();

    public void testEnsureValid() {
        batchSizeValidator.ensureValid("CONFIG_BATCH_SIZE", 10);
    }

    public void testEnsureValidBatchOverSized() {
        try {
            batchSizeValidator.ensureValid("CONFIG_BATCH_SIZE", 101);
            fail("It should have a KafkaException here.");
        } catch (Exception ex) {
            if (!(ex instanceof KafkaException)) {
                fail("There is an exception but it is not a KafkaException.");
            }
        }
    }

    public void testEnsureValidBatchLowerSized() {
        try {
            batchSizeValidator.ensureValid("CONFIG_BATCH_SIZE", -1);
            fail("It should have a KafkaException here.");
        } catch (Exception ex) {
            if (!(ex instanceof KafkaException)) {
                fail("There is an exception but it is not a KafkaException.");
            }
        }
    }
}