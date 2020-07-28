package com.github.felipegutierrez.kafka.consumer;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ElasticSearchConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class);
    private final String elasticSearchCredentialFile = "elasticsearch.token";
    private String hostname;
    private String username;
    private String password;

    public ElasticSearchConsumer() {
        leadCredentials();
        createClient();
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
}
