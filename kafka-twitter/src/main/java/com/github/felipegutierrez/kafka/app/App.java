package com.github.felipegutierrez.kafka.app;

import com.github.felipegutierrez.kafka.producers.TwitterProducer;
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
                    System.out.println("App 7 selected: " + TwitterProducer.class.getSimpleName());
                    TwitterProducer twitterProducer = new TwitterProducer(elements);
                    twitterProducer.run();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + TwitterProducer.class.getSimpleName());
            logger.info("use: java -jar kafka-twitter/target/kafka-twitter-1.0.jar -app 7 -elements \"corona|covid|covid-19\"");
        }
    }
}
