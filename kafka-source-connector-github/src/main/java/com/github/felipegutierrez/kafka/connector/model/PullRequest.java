package com.github.felipegutierrez.kafka.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.felipegutierrez.kafka.connector.schemas.GitHubSchemas.PR_HTML_URL_FIELD;
import static com.github.felipegutierrez.kafka.connector.schemas.GitHubSchemas.PR_URL_FIELD;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PullRequest {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private String url;
    private String htmlUrl;
    private String diffUrl;
    private String patchUrl;

    public static PullRequest fromJson(JSONObject pull_request) {
        return new PullRequest()
                .setUrl(pull_request.getString(PR_URL_FIELD))
                .setHtmlUrl(pull_request.getString(PR_HTML_URL_FIELD));
    }
}
