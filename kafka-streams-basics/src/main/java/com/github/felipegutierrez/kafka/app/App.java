package com.github.felipegutierrez.kafka.app;

import com.github.felipegutierrez.kafka.connector.stream.twitter.KafkaStreamFilterTweets;
import com.github.felipegutierrez.kafka.connector.stream.udemy.KafkaStreamUdemyAggregator;
import com.github.felipegutierrez.kafka.connector.stream.udemy.KafkaStreamUdemyFraudDetector;
import com.github.felipegutierrez.kafka.util.Parameters;
import com.github.felipegutierrez.kafka.stream.bankbalance.BankBalanceExactlyOnce;
import com.github.felipegutierrez.kafka.stream.bankbalance.BankTransactionsProducer;
import com.github.felipegutierrez.kafka.stream.colour.FavouriteColourApp;
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
                    System.out.println("App 1 selected: " + KafkaStreamFilterTweets.class.getSimpleName());
                    new KafkaStreamFilterTweets();
                    app = 0;
                    break;
                case 2:
                    System.out.println("App 2 selected: " + KafkaStreamUdemyFraudDetector.class.getSimpleName());
                    KafkaStreamUdemyFraudDetector fraudDetector = new KafkaStreamUdemyFraudDetector();
                    fraudDetector.start();
                    app = 0;
                    break;
                case 3:
                    System.out.println("App 3 selected: " + KafkaStreamUdemyAggregator.class.getSimpleName());
                    KafkaStreamUdemyAggregator aggregator = new KafkaStreamUdemyAggregator();
                    aggregator.start();
                    app = 0;
                    break;
                case 4:
                    System.out.println("App 4 selected: " + FavouriteColourApp.class.getSimpleName());
                    FavouriteColourApp favouriteColourApp = new FavouriteColourApp();
                    app = 0;
                    break;
                case 5:
                    System.out.println("App 5 selected: " + BankTransactionsProducer.class.getSimpleName());
                    BankTransactionsProducer bankTransactionsProducer = new BankTransactionsProducer();
                    app = 0;
                    break;
                case 6:
                    System.out.println("App 6 selected: " + BankBalanceExactlyOnce.class.getSimpleName());
                    BankBalanceExactlyOnce bankBalanceExactlyOnce = new BankBalanceExactlyOnce();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + KafkaStreamFilterTweets.class.getSimpleName());
            logger.info("2 - " + KafkaStreamUdemyFraudDetector.class.getSimpleName());
            logger.info("3 - " + KafkaStreamUdemyAggregator.class.getSimpleName());
            logger.info("4 - " + FavouriteColourApp.class.getSimpleName());
            logger.info("5 - " + BankTransactionsProducer.class.getSimpleName());
            logger.info("6 - " + BankBalanceExactlyOnce.class.getSimpleName());
            logger.info("use: java -jar kafka-streams-basics/target/kafka-streams-basics-1.0.jar -app [1|2|3]");
        }
    }
}
