package com.github.felipegutierrez.kafka.connector.stream.app;

import com.github.felipegutierrez.kafka.elasticsearch.consumer.ElasticSearchConsumer;
import com.github.felipegutierrez.kafka.elasticsearch.consumer.ElasticSearchConsumerWithBulkRequest;
import com.github.felipegutierrez.kafka.elasticsearch.consumer.ElasticSearchConsumerWithIdempotentRequests;
import com.github.felipegutierrez.kafka.elasticsearch.consumer.ElasticSearchConsumerWithIdempotentRequestsAndSyncbatchCommit;
import com.github.felipegutierrez.kafka.util.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int app = 0;
        if (args != null && args.length > 0) {
            int size = args.length;
            String elements = "";
            for (int i = 0; i < size; i++) {
                if (Parameters.APP.equals(String.valueOf(args[i])) && i + 1 < size) {
                    i++;
                    app = Integer.parseInt(args[i]);
                } else if (Parameters.ELEMENTS.equals(String.valueOf(args[i])) && i + 1 < size) {
                    i++;
                    elements = args[i];
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
                    System.out.println("App 1 selected: " + ElasticSearchConsumer.class.getSimpleName());
                    new ElasticSearchConsumer();
                    app = 0;
                    break;
                case 2:
                    System.out.println("App 2 selected: " + ElasticSearchConsumerWithIdempotentRequests.class.getSimpleName());
                    new ElasticSearchConsumerWithIdempotentRequests();
                    app = 0;
                    break;
                case 3:
                    System.out.println("App 3 selected: " + ElasticSearchConsumerWithIdempotentRequestsAndSyncbatchCommit.class.getSimpleName());
                    new ElasticSearchConsumerWithIdempotentRequestsAndSyncbatchCommit();
                    app = 0;
                    break;
                case 4:
                    System.out.println("App 4 selected: " + ElasticSearchConsumerWithBulkRequest.class.getSimpleName());
                    new ElasticSearchConsumerWithBulkRequest();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + ElasticSearchConsumer.class.getSimpleName());
            logger.info("2 - " + ElasticSearchConsumerWithIdempotentRequests.class.getSimpleName());
            logger.info("3 - " + ElasticSearchConsumerWithIdempotentRequestsAndSyncbatchCommit.class.getSimpleName());
            logger.info("3 - " + ElasticSearchConsumerWithBulkRequest.class.getSimpleName());
            logger.info("use: java -jar kafka-elasticsearch/target/kafka-elasticsearch-1.0.jar -app [1|2|3|4]");
        }
    }
}
