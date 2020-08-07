package com.github.felipegutierrez.avro.producer;

import com.github.felipegutierrez.avro.udemy.Review;
import com.github.felipegutierrez.runnable.ReviewsAvroProducerThread;
import com.github.felipegutierrez.runnable.ReviewsFetcherThread;
import com.github.felipegutierrez.util.UdemyConfig;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class UdemyReviewsProducerAvroMain {
    private final Logger log = LoggerFactory.getLogger(UdemyReviewsProducerAvroMain.class.getSimpleName());

    // thread safe queue which blocks when full.
    private final ExecutorService executor;
    private final CountDownLatch latch;
    private final ReviewsFetcherThread udemyRESTClient;
    private final ReviewsAvroProducerThread reviewsProducer;

    public UdemyReviewsProducerAvroMain() {
        disclaimer();
        UdemyConfig appConfig = new UdemyConfig(ConfigFactory.load());
        latch = new CountDownLatch(2);
        executor = Executors.newFixedThreadPool(2);
        ArrayBlockingQueue<Review> reviewsQueue = new ArrayBlockingQueue<>(appConfig.getQueueCapacity());
        udemyRESTClient = new ReviewsFetcherThread(appConfig, reviewsQueue, latch);
        reviewsProducer = new ReviewsAvroProducerThread(appConfig, reviewsQueue, latch);
    }

    public static void main(String[] args) {
        UdemyReviewsProducerAvroMain app = new UdemyReviewsProducerAvroMain();
        app.start();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!executor.isShutdown()) {
                log.info("Shutdown requested");
                shutdown();
            }
        }));

        log.info("Application started!");
        executor.submit(udemyRESTClient);
        executor.submit(reviewsProducer);
        log.info("Stuff submit");
        try {
            log.info("Latch await");
            latch.await();
            log.info("Threads completed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
            log.info("Application closed successfully");
        }
    }

    public void shutdown() {
        if (!executor.isShutdown()) {
            log.info("Shutting down");
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(2000, TimeUnit.MILLISECONDS)) { //optional *
                    log.warn("Executor did not terminate in the specified time."); //optional *
                    List<Runnable> droppedTasks = executor.shutdownNow(); //optional **
                    log.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); //optional **
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void disclaimer() {
        log.info("");

        log.info("Download Confluent Platform 5.1.1 https://www.confluent.io/download/");
        log.info("Unzip and add confluent-5.1.1/bin to your PATH");
        log.info("Download and install Docker for Mac / Windows / Linux and do");
        log.info("docker-compose up -d");
        log.info("Start the Confluent platform using the Confluent CLI");
        log.info("confluent start");
        log.info("export COURSE_ID=1075642  # Kafka for Beginners Course");
        log.info("kafka-topics --create --topic udemy-reviews --zookeeper localhost:2181 --partitions 3 --replication-factor 1 --from-beginning");
        log.info("");
        log.info("");
        log.info("");
    }
}
