package com.github.felipegutierrez.kafka;

import com.github.felipegutierrez.kafka.basics.consumers.ConsumerDemo;
import com.github.felipegutierrez.kafka.basics.consumers.ConsumerDemoAssignSeek;
import com.github.felipegutierrez.kafka.basics.consumers.ConsumerDemoWithThreads;
import com.github.felipegutierrez.kafka.basics.producers.ProducerAsync;
import com.github.felipegutierrez.kafka.basics.producers.ProducerAsyncCallback;
import com.github.felipegutierrez.kafka.util.Parameters;
import com.github.felipegutierrez.kafka.util.RecordUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
                    System.out.println("App 1 selected: " + ProducerAsync.class.getSimpleName());
                    ProducerAsync producerAsync = new ProducerAsync();
                    producerAsync.sendData(new ProducerRecord<String, String>("first-topic", "01, hello world!"));
                    app = 0;
                    break;
                case 2:
                    System.out.println("App 2 selected: " + ProducerAsyncCallback.class.getSimpleName());
                    ProducerAsyncCallback producerAsyncCallback = new ProducerAsyncCallback();
                    List<ProducerRecord<String, String>> list = RecordUtils.getProducerRecordList("first-topic", "hello world", 10);
                    producerAsyncCallback.sendData(list);
                    producerAsyncCallback.closeProducer();
                    app = 0;
                    break;
                case 3:
                    System.out.println("App 3 selected: " + ProducerAsyncCallback.class.getSimpleName());
                    ProducerAsyncCallback producerAsyncCallbackWithKeys = new ProducerAsyncCallback();
                    List<ProducerRecord<String, String>> listWithKeys = RecordUtils.getProducerRecordWithKeyList("first-topic", "hello world", 10);
                    producerAsyncCallbackWithKeys.sendData(listWithKeys);
                    producerAsyncCallbackWithKeys.closeProducer();
                    app = 0;
                    break;
                case 4:
                    System.out.println("App 4 selected: " + ConsumerDemo.class.getSimpleName());
                    new ConsumerDemo();
                    app = 0;
                    break;
                case 5:
                    System.out.println("App 5 selected: " + ConsumerDemoWithThreads.class.getSimpleName());
                    new ConsumerDemoWithThreads();
                    app = 0;
                    break;
                case 6:
                    System.out.println("App 6 selected: " + ConsumerDemoAssignSeek.class.getSimpleName());
                    new ConsumerDemoAssignSeek();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        } else {
            logger.info("Applications available");
            logger.info("1 - " + ProducerAsync.class.getSimpleName());
            logger.info("2 - " + ProducerAsyncCallback.class.getSimpleName());
            logger.info("3 - " + ProducerAsyncCallback.class.getSimpleName() + " with keys");
            logger.info("4 - " + ConsumerDemo.class.getSimpleName());
            logger.info("5 - " + ConsumerDemoWithThreads.class.getSimpleName());
            logger.info("6 - " + ConsumerDemoAssignSeek.class.getSimpleName());
            logger.info("use: java -jar kafka-basics/target/kafka-basics-1.0.jar -app [1|2|3|4|5|6]");
        }
    }
}
