package com.github.felipegutierrez.avro.app;

import com.github.felipegutierrez.avro.generic.GenericRecordExamples;
import com.github.felipegutierrez.avro.util.Parameters;
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
                    System.out.println("App 1 selected: " + GenericRecordExamples.class.getSimpleName());
                    new GenericRecordExamples();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + GenericRecordExamples.class.getSimpleName());
            logger.info("use: java -jar kafka-basics/target/kafka-basics-1.0.jar -app [1|2|3|4|5|6]");
        }
    }
}
