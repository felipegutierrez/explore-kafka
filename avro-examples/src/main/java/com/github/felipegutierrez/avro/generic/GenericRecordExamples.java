package com.github.felipegutierrez.avro.generic;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.*;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GenericRecordExamples {
    private static final Logger logger = LoggerFactory.getLogger(GenericRecordExamples.class);

    public GenericRecordExamples() {

        // step 0: define schema
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse("{\n" +
                "     \"type\": \"record\",\n" +
                "     \"namespace\": \"com.example\",\n" +
                "     \"name\": \"Customer\",\n" +
                "     \"doc\": \"Avro Schema for our Customer\",     \n" +
                "     \"fields\": [\n" +
                "       { \"name\": \"first_name\", \"type\": \"string\", \"doc\": \"First Name of Customer\" },\n" +
                "       { \"name\": \"last_name\", \"type\": \"string\", \"doc\": \"Last Name of Customer\" },\n" +
                "       { \"name\": \"age\", \"type\": \"int\", \"doc\": \"Age at the time of registration\" },\n" +
                "       { \"name\": \"height\", \"type\": \"float\", \"doc\": \"Height at the time of registration in cm\" },\n" +
                "       { \"name\": \"weight\", \"type\": \"float\", \"doc\": \"Weight at the time of registration in kg\" },\n" +
                "       { \"name\": \"automated_email\", \"type\": \"boolean\", \"default\": true, \"doc\": \"Field indicating if the user is enrolled in marketing emails\" }\n" +
                "     ]\n" +
                "}");

        // step 1: create a generic record
        GenericRecordBuilder customerBuilder = new GenericRecordBuilder(schema);
        customerBuilder.set("first_name", "John");
        customerBuilder.set("last_name", "Doe");
        customerBuilder.set("age", 25);
        customerBuilder.set("height", 170f);
        customerBuilder.set("weight", 80.5f);
        customerBuilder.set("automated_email", false);
        GenericData.Record customer = customerBuilder.build();
        logger.info("customer: " + customer);

        // we build our second customer which has defaults
        GenericRecordBuilder customerBuilderWithDefault = new GenericRecordBuilder(schema);
        customerBuilderWithDefault.set("first_name", "Johnny");
        customerBuilderWithDefault.set("last_name", "Deepy");
        customerBuilderWithDefault.set("age", 39);
        customerBuilderWithDefault.set("height", 180f);
        customerBuilderWithDefault.set("weight", 81.5f);
        GenericData.Record customerWithDefault = customerBuilderWithDefault.build();
        logger.info("customerWithDefault: " + customerWithDefault);

        // step 2: write that generic record to a file
        final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            File directory = new File("/tmp/avro-output/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            dataFileWriter.create(customer.getSchema(), new File("/tmp/avro-output/customer-generic.avro"));
            dataFileWriter.append(customer);
            dataFileWriter.append(customerWithDefault);
            logger.info("Written /tmp/avro-output/customer-generic.avro");
            dataFileWriter.close();
        } catch (AvroRuntimeException are) {
            logger.error("Avro Runtime error: ", are.getMessage());
            are.printStackTrace();
        } catch (IOException e) {
            logger.error("Couldn't write file: ", e.getMessage());
            e.printStackTrace();
        }

        // step 3: read a generic record from a file
        final File file = new File("/tmp/avro-output/customer-generic.avro");
        final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        GenericRecord customerRead;
        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, datumReader)) {
            logger.info("Successfully read avro file");
            while (dataFileReader.hasNext()) {
                customerRead = dataFileReader.next();
                logger.info(customerRead.toString());

                // step 4: interpret as a generic record
                try {
                    // get the data from the generic record
                    logger.info("First name: " + customerRead.get("first_name"));

                    // read a non existent field
                    logger.info("Non existent field: " + customerRead.get("not_here"));
                } catch (AvroRuntimeException are) {
                    logger.error("Avro Runtime error: ", are.getStackTrace());
                    are.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error("Couldn't read file: ", e.getMessage());
            e.printStackTrace();
        }
    }
}
