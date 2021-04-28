package com.github.felipegutierrez.kafka.connector.connectors;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.felipegutierrez.kafka.connector.config.GitHubSourceConnectorConfig.*;
import static org.junit.Assert.assertEquals;

public class GitHubSourceConnectorTest {

    private Map<String, String> initialConfig() {
        Map<String, String> baseProps = new HashMap<>();
        baseProps.put(OWNER_CONFIG, "foo");
        baseProps.put(REPO_CONFIG, "bar");
        baseProps.put(SINCE_CONFIG, "2017-04-26T01:23:45Z");
        baseProps.put(BATCH_SIZE_CONFIG, "100");
        baseProps.put(TOPIC_CONFIG, "github-issues");
        baseProps.put(GROUP_ID_CONFIG, "1");
        baseProps.put(OFFSET_STORAGE_TOPIC_CONFIG, "offset-storage-topic");
        return (baseProps);
    }

    @Test
    public void taskConfigsShouldReturnOneTaskConfig() {
        GitHubSourceConnector gitHubSourceConnector = new GitHubSourceConnector();
        gitHubSourceConnector.start(initialConfig());
        assertEquals(gitHubSourceConnector.taskConfigs(1).size(), 1);
        assertEquals(gitHubSourceConnector.taskConfigs(10).size(), 1);
    }
}
