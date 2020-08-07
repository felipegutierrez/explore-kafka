package com.github.felipegutierrez.kafka.registry.avro.specific;

import com.example.Customer;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
        final DatumWriter<Customer> datumWriter = new SpecificDatumWriter<>(Customer.class);

        try (DataFileWriter<Customer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            File directory = new File("/tmp/avro-output/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            dataFileWriter.create(customer.getSchema(), new File("/tmp/avro-output/customer-specific.avro"));
            dataFileWriter.append(customer);
            logger.info("successfully wrote /tmp/avro-output/customer-specific.avro");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // step 3: read from a file
        final File file = new File("/tmp/avro-output/customer-specific.avro");
        final DatumReader<Customer> datumReader = new SpecificDatumReader<>(Customer.class);
        final DataFileReader<Customer> dataFileReader;
        try {
            logger.info("Reading our specific record");
            dataFileReader = new DataFileReader<>(file, datumReader);
            while (dataFileReader.hasNext()) {
                Customer readCustomer = dataFileReader.next();
                logger.info(readCustomer.toString());

                // step 4: interpret
                logger.info("First name: " + readCustomer.getFirstName());
                logger.info("age: " + readCustomer.getAge());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
