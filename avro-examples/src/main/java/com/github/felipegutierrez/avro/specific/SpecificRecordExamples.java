package com.github.felipegutierrez.avro.specific;

import com.example.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The avro schema from resources/avro/customer.avsc generates the Customer POJO to us.
 * Then avro is now Type safe =).
 *
 * @see avro/customer.avsc
 */
public class SpecificRecordExamples {
    private static final Logger logger = LoggerFactory.getLogger(SpecificRecordExamples.class);
    Customer.Builder customerBuilder = Customer.newBuilder();

    public SpecificRecordExamples() {
        // step 1: create specific record

        customerBuilder.setAge(30);
        customerBuilder.setFirstName("Mark");
        customerBuilder.setLastName("Simpson");
        customerBuilder.setAutomatedEmail(true);
        customerBuilder.setHeight(180f);
        customerBuilder.setWeight(90f);
        Customer customer = customerBuilder.build();
        logger.info(customer.toString());
        clearCustomer();

        customerBuilder.setAge(25);
        customerBuilder.setFirstName("John");
        customerBuilder.setLastName("Doe");
        customerBuilder.setHeight(180f);
        customerBuilder.setWeight(90f);
        Customer customer01 = customerBuilder.build();
        logger.info(customer01.toString());
        clearCustomer();

        try {
            customerBuilder.setAge(40);
            customerBuilder.setFirstName("Johnny");
            customerBuilder.setHeight(190f);
            customerBuilder.setWeight(90f);
            Customer customer02 = customerBuilder.build();
            logger.info(customer02.toString());
        } catch (RuntimeException re) {
            re.printStackTrace();
        }

        // step 2: write to a file

        // step 3: read from a file

        // step 4: interpret

    }

    private void clearCustomer() {
        customerBuilder.clearFirstName();
        customerBuilder.clearLastName();
        customerBuilder.clearAutomatedEmail();
        customerBuilder.clearAge();
        customerBuilder.clearHeight();
        customerBuilder.clearHeight();
    }
}
