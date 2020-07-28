package com.github.felipegutierrez.kafka.producers;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterProducer {
    private final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);
    private final String twitterAccessTokenFile = "twitter.access.token";
    private String consumerKey;
    private String consumerSecret;
    private String token;
    private String secret;

    public TwitterProducer() {
        loadTokens();
    }

    public void run() {
        // create a twitter producer
        createTwitterClient();

        // create a kafka producer

        // loop and send tweets to kafka

    }

    private void loadTokens() {
        InputStream in = getClass().getClassLoader().getResourceAsStream(twitterAccessTokenFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            int count = 0;
            while (reader.ready()) {
                count++;
                String line = reader.readLine();
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    if (count == 1) consumerKey = tokens[1];
                    if (count == 2) consumerSecret = tokens[1];
                    if (count == 3) token = tokens[1];
                    if (count == 4) secret = tokens[1];
                } else {
                    throw new IOException();
                }
            }
            logger.info("Tokens read. consumerKey: " + consumerKey + ", consumerSecret: " + consumerSecret +
                    ", token: " + token + ", secret: " + secret);
        } catch (NullPointerException | FileNotFoundException e) {
            logger.error("File [" + twitterAccessTokenFile + "] not found.");
        } catch (IOException e) {
            logger.error("File [" + twitterAccessTokenFile + "] has wrong format. Please use: \n" +
                    "API key:xxxxxxxxxxxxxxxxxxx\n" +
                    "API secret key:xxxxxxxxxxxxxxxxxxx\n" +
                    "Access token:xxxxxxxxxxxxxxxxxxx\n" +
                    "Access token secret:xxxxxxxxxxxxxxxxxxx");
        }
    }

    private void createTwitterClient() {

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("kafka");
        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
    }
}
