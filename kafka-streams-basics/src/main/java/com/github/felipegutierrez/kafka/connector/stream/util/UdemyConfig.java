package com.github.felipegutierrez.kafka.connector.stream.util;

import com.typesafe.config.Config;

public class UdemyConfig {
    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String sourceTopicName;
    private final String validTopicName;
    private final String fraudTopicName;
    private final String applicationFraudId;
    private final String recentStatsTopicName;
    private final String longTermStatsStatsTopicName;
    private final String applicationAggregateId;

    public UdemyConfig(Config config) {
        this.bootstrapServers = config.getString("kafka.bootstrap.servers");
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url");
        this.sourceTopicName = config.getString("kafka.source.topic.name");
        this.validTopicName = config.getString("kafka.valid.topic.name");
        this.fraudTopicName = config.getString("kafka.fraud.topic.name");
        this.recentStatsTopicName = config.getString("kafka.recent.stats.topic.name");
        this.longTermStatsStatsTopicName = config.getString("kafka.long.term.stats.topic.name");
        this.applicationFraudId = config.getString("kafka.streams.application.fraud.id");
        this.applicationAggregateId = config.getString("kafka.streams.application.aggregate.id");
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public String getSourceTopicName() {
        return sourceTopicName;
    }

    public String getValidTopicName() {
        return validTopicName;
    }

    public String getFraudTopicName() {
        return fraudTopicName;
    }

    public String getApplicationFraudId() { return applicationFraudId; }

    public String getApplicationAggregateId() {
        return applicationAggregateId;
    }

    public String getRecentStatsTopicName() { return recentStatsTopicName; }

    public String getLongTermStatsStatsTopicName() { return longTermStatsStatsTopicName; }
}
