package com.github.felipegutierrez.kafka.elasticsearch.consumer;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ElasticSearchConsumerWithBulkRequest {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConsumerWithBulkRequest.class);
    private final String elasticSearchCredentialFile = "elasticsearch.token";
    private final String bootstrapServers = "127.0.0.1:9092";
    private final String groupId = "kafka-demo-elasticsearch";
    private final String topic = "twitter_tweets";
    private final JsonParser jsonParser = new JsonParser();
    private int maxInsert;
    private boolean insertIntoElasticsearch = false;
    private String hostname;
    private String username;
    private String password;

    public ElasticSearchConsumerWithBulkRequest() {
        this(-1);
    }

    public ElasticSearchConsumerWithBulkRequest(int maxInsert) {
        try {
            this.maxInsert = maxInsert;
            disclaimer();
            leadCredentials();
            RestHighLevelClient client = createClient();

            KafkaConsumer<String, String> consumer = createConsumer();
            int count = 0;
            // poll for new data
            while (insertIntoElasticsearch) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                int recordsCount = records.count();
                logger.info("Received: " + recordsCount + " records");

                // the BulkRequest
                BulkRequest bulkRequest = new BulkRequest();

                for (ConsumerRecord<String, String> record : records) {
                    // logger.info("Key:" + record.key() + " Value:" + record.value() + " Partition:" + record.partition() + " Offset:" + record.offset());
                    // insert data into elasticsearch
                    String jsonString = record.value(); // "{\"foo\": \"bar\"}";

                    // pass ID to the IndexRequest to make it idempotent. Strategy 1 create an ID. Strategy 2 get the ID from the tweet.
                    // String id = record.topic() + "_" + record.partition() + "_" + record.offset();
                    try {
                        String id = extractIdFromTweet(record.value());

                        // make sure that the index id exist at https://app.bonsai.io/clusters/kafka-5082250343/console
                        IndexRequest indexResquest = new IndexRequest("twitter", "tweets", id).source(jsonString, XContentType.JSON);

                        // add to the bulk request in async manner
                        bulkRequest.add(indexResquest);
                    } catch (NullPointerException npe) {
                        logger.warn("Skipping bad data due to null twitter ID: " + record.value());
                    }
                }
                if (recordsCount > 0) {
                    logger.info("creating a bulk response");
                    BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    logger.info("Committing the offsets");
                    consumer.commitSync();
                    logger.info("Offsets have been committed");
                    count++;
                    if (maxInsert != -1 && count >= maxInsert) {
                        insertIntoElasticsearch = false;
                        break;
                    }
                    Thread.sleep(1000); // introduce a small delay
                }
            }

            // close the elasticsearch client
            client.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void leadCredentials() {
        logger.info("Loading ElasticSearch bonzai.io credentials from [" + elasticSearchCredentialFile + "]");
        InputStream in = getClass().getClassLoader().getResourceAsStream(elasticSearchCredentialFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            int count = 0;
            while (reader.ready()) {
                count++;
                String line = reader.readLine();
                String[] tokens = line.split("@");
                if (tokens.length == 2) {
                    String[] userPass = tokens[0].split(":");
                    if (userPass.length == 3) {
                        this.username = userPass[1].replace("//", "");
                        this.password = userPass[2];
                    }
                    String[] host = tokens[1].split(":");
                    if (host.length == 2) {
                        this.hostname = host[0];
                    }
                } else {
                    throw new IOException();
                }
            }
            insertIntoElasticsearch = true;
            logger.info("Tokens read. username: " + username + ", password: *********, hostname: " + hostname);
        } catch (NullPointerException | FileNotFoundException e) {
            logger.error("File [" + elasticSearchCredentialFile + "] not found.");
        } catch (IOException e) {
            logger.error("File [" + elasticSearchCredentialFile + "] has wrong format. Please use: https://USERNAME:PASSWORD@HOSTNAME:443");
        }
    }

    private RestHighLevelClient createClient() {
        // do not execute this if you are running a local ElasticSearch
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder builder = RestClient
                .builder(new HttpHost(hostname, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    private KafkaConsumer<String, String> createConsumer() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // disable auto commit of offsets
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());
        // guarantee that we receive only 100 records because we are asynch using BulkRequest
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");

        // mechanism to detect a consumer application being down
        // heartbeats are sent periodically to the broker.
        // If no heartbeat is sent during that period the consumer is considered dead.
        // set even lower to faster consumer balances
        // properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "3000"); // default: 10,000 milliseconds
        // how ofter to send heartbeats. Usually it is set to 1/3 of the SESSION_TIMEOUT_MS_CONFIG
        // properties.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "1000"); // default: 3,000 milliseconds

        // when using a bigData process engine decrease this parameter
        // maximum amount of time between two poll() calls before declaring the consumer dead.
        // properties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "1000"); // default: 5,000 milliseconds

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));

        return consumer;
    }

    private String extractIdFromTweet(String tweetJson) {
        return jsonParser.parse(tweetJson).getAsJsonObject().get("id_str").getAsString();
    }

    private void disclaimer() {
        logger.info("Start zookeeper: ./bin/zookeeper-server-start.sh config/zookeeper.properties");
        logger.info("Start the broker: ./bin/kafka-server-start.sh config/server.properties");
        logger.info("remove the topic: ./bin/kafka-topics.sh --delete --topic twitter_tweets --zookeeper localhost:2181");
        logger.info("create the topic: ./bin/kafka-topics.sh --create --topic twitter_tweets --zookeeper localhost:2181 --partitions 6 --replication-factor 1");
        logger.info("Start the consumer: java -jar kafka-twitter/target/kafka-twitter-1.0.jar -app 1 -elements \"felipe\"");
        logger.info("start the consumer from console: ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_tweets");
        logger.info("describe the group-id to check that the offset is idempotent: ./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group kafka-demo-elasticsearch --describe");
        logger.info("reset the offsets: ./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group kafka-demo-elasticsearch --reset-offsets --execute --to-earliest --topic twitter_tweets");
        logger.info("");
        logger.info("");
    }
}
