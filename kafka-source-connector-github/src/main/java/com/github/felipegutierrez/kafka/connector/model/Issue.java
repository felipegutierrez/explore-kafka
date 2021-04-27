package com.github.felipegutierrez.kafka.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.felipegutierrez.kafka.connector.schemas.GitHubSchemas.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Issue {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private Integer id;
    private String url;
    private String repositoryUrl;
    private String labelsUrl;
    private String commentsUrl;
    private String eventsUrl;
    private String htmlUrl;
    private Integer number;
    private String state;
    private String title;
    private String body;
    private User user;
    private List<Label> labels = null;
    private Assignee assignee;
    private Milestone milestone;
    private Boolean locked;
    private Integer comments;
    private PullRequest pullRequest;
    private Object closedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Assignee> assignees = null;

    public static Issue fromJson(JSONObject jsonObject) {

        // user is mandatory
        User user = User.fromJson(jsonObject.getJSONObject(USER_FIELD));

        Issue issue = new Issue()
                .setUrl(jsonObject.getString(URL_FIELD))
                .setHtmlUrl(jsonObject.getString(HTML_URL_FIELD))
                .setTitle(jsonObject.getString(TITLE_FIELD))
                .setCreatedAt(Instant.parse(jsonObject.getString(CREATED_AT_FIELD)))
                .setUpdatedAt(Instant.parse(jsonObject.getString(UPDATED_AT_FIELD)))
                .setNumber(jsonObject.getInt(NUMBER_FIELD))
                .setState(jsonObject.getString(STATE_FIELD))
                .setUser(user);

        // pull request is an optional fields
        if (jsonObject.has(PR_FIELD)) {
            PullRequest pullRequest = PullRequest.fromJson(jsonObject.getJSONObject(PR_FIELD));
            issue.setPullRequest(pullRequest);
        }
        return issue;
    }
}
