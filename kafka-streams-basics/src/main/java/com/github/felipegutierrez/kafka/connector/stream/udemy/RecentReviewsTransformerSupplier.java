package com.github.felipegutierrez.kafka.connector.stream.udemy;

import com.github.felipegutierrez.kafka.registry.avro.udemy.Review;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;

public class RecentReviewsTransformerSupplier implements TransformerSupplier<String, Review, KeyValue<String, Review>> {

    private final Long timeToKeepAReview;
    private final String stateStoreName;
    private final RecentReviewsTransformer recentReviewsTransformer;

    public RecentReviewsTransformerSupplier(Long timeToKeepAReview, String stateStoreName) {
        this.timeToKeepAReview = timeToKeepAReview;
        this.stateStoreName = stateStoreName;
        this.recentReviewsTransformer = new RecentReviewsTransformer(timeToKeepAReview, stateStoreName);
    }

    @Override
    public Transformer<String, Review, KeyValue<String, Review>> get() {
        return recentReviewsTransformer;
    }
}
