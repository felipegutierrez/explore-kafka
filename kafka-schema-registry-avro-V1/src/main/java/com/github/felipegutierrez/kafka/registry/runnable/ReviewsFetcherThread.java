package com.github.felipegutierrez.kafka.registry.runnable;

import com.github.felipegutierrez.kafka.registry.avro.udemy.Review;
import com.github.felipegutierrez.kafka.registry.client.UdemyRESTClient;
import com.github.felipegutierrez.kafka.registry.util.UdemyConfig;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class ReviewsFetcherThread implements Runnable {
    private final Logger log = LoggerFactory.getLogger(ReviewsFetcherThread.class.getSimpleName());

    private final UdemyConfig appConfig;
    private final ArrayBlockingQueue<Review> reviewsQueue;
    private final CountDownLatch latch;
    private final UdemyRESTClient udemyRESTClient;

    public ReviewsFetcherThread(UdemyConfig appConfig, ArrayBlockingQueue<Review> reviewsQueue, CountDownLatch latch) {
        this.appConfig = appConfig;
        this.reviewsQueue = reviewsQueue;
        this.latch = latch;
        udemyRESTClient = new UdemyRESTClient(appConfig.getCourseId(), appConfig.getUdemyPageSize());
    }

    @Override
    public void run() {
        try {
            Boolean keepOnRunning = true;
            while (keepOnRunning) {
                List<Review> reviews;
                try {
                    reviews = udemyRESTClient.getNextReviews();
                    log.info("Fetched " + reviews.size() + " reviews");
                    if (reviews.size() == 0) {
                        Thread.sleep(1000);
                        // keepOnRunning = false;
                    } else {
                        // this may block if the queue is full - this is flow control
                        log.info("Queue size :" + reviewsQueue.size());
                        for (Review review : reviews) {
                            reviewsQueue.put(review);
                        }
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    Thread.sleep(500);
                } finally {
                    Thread.sleep(50);
                }
            }
        } catch (InterruptedException e) {
            log.warn("REST Client interrupted");
        } finally {
            this.close();
        }
    }

    private void close() {
        log.info("Closing");
        udemyRESTClient.close();
        latch.countDown();
        log.info("Closed");
    }
}
