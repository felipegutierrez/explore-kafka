package com.github.felipegutierrez.kafka.app;

import com.github.felipegutierrez.kafka.ConsumerDemo;
import com.github.felipegutierrez.kafka.producers.ProducerAsync;
import com.github.felipegutierrez.kafka.producers.ProducerAsyncCallback;
import com.github.felipegutierrez.kafka.producers.ProducerAsyncCallbackKeys;
import com.github.felipegutierrez.kafka.util.Parameters;

public class App {
    public static void main(String[] args) {
        int app = 0;
        if (args != null && args.length > 0) {
            int size = args.length;
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
                    System.out.println("App 1 selected: " + ProducerAsync.class.getSimpleName());
                    new ProducerAsync();
                    app = 0;
                    break;
                case 2:
                    System.out.println("App 2 selected: " + ProducerAsyncCallback.class.getSimpleName());
                    new ProducerAsyncCallback();
                    app = 0;
                    break;
                case 3:
                    System.out.println("App 3 selected: " + ProducerAsyncCallbackKeys.class.getSimpleName());
                    new ProducerAsyncCallbackKeys();
                    app = 0;
                    break;
                case 4:
                    System.out.println("App 4 selected: " + ConsumerDemo.class.getSimpleName());
                    new ConsumerDemo();
                    app = 0;
                    break;
                default:
                    args = null;
                    System.out.println("No application selected [" + app + "] ");
                    break;
            }
        }
    }
}
