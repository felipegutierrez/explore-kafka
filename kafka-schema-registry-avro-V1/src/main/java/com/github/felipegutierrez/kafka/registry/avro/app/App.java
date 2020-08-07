package com.github.felipegutierrez.kafka.registry.avro.app;

import com.github.felipegutierrez.kafka.registry.avro.consumer.KafkaAvroConsumerV1;
import com.github.felipegutierrez.kafka.registry.avro.producer.KafkaAvroProducerV1;
import com.github.felipegutierrez.kafka.registry.avro.producer.UdemyReviewsProducerAvroMain;
import com.github.felipegutierrez.kafka.registry.avro.util.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int app = 0;
        if (args != null && args.length > 0) {
            int size = args.length;
            String offset = "earliest";
            for (int i = 0; i < size; i++) {
                if (Parameters.APP.equals(String.valueOf(args[i])) && i + 1 < size) {
                    i++;
                    app = Integer.parseInt(args[i]);
                } else if (Parameters.OFFSET.equals(String.valueOf(args[i])) && i + 1 < size) {
                    i++;
                    offset = args[i];
                }
            }
            System.out.println();
            System.out.println("Parameters chosen >>");
            System.out.println("Application selected    : " + app);

            switch (app) {
                case 0:
                    System.out.println("Parameters missing! Please launch the application following the example below.");
                    System.out.println();
                    System.out.println("bis später");
                    break;
                case 1:
                    System.out.println("App 1 selected: " + KafkaAvroProducerV1.class.getSimpleName());
                    new KafkaAvroProducerV1();
                    app = 0;
                    break;
                case 2:
                    System.out.println("App 2 selected: " + KafkaAvroConsumerV1.class.getSimpleName());
                    new KafkaAvroConsumerV1(offset);
                    app = 0;
                    break;
                case 3:
                    System.out.println("App 3 selected: " + UdemyReviewsProducerAvroMain.class.getSimpleName());
                    UdemyReviewsProducerAvroMain udemyReviewsProducerAvroMain = new UdemyReviewsProducerAvroMain();
                    udemyReviewsProducerAvroMain.start();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + KafkaAvroProducerV1.class.getSimpleName());
            logger.info("2 - " + KafkaAvroConsumerV1.class.getSimpleName());
            logger.info("3 - " + UdemyReviewsProducerAvroMain.class.getSimpleName());
            logger.info("use: java -jar kafka-schema-registry-avro-V1/target/kafka-schema-registry-avro-V1-1.0.jar -app [1|2|3]");
        }
    }
}
