package com.github.felipegutierrez.kafka.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.felipegutierrez.kafka.connector.schemas.GitHubSchemas.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class User {
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private String login;
    private Integer id;
    private String avatarUrl;
    private String gravatarId;
    private String url;
    private String htmlUrl;
    private String followersUrl;
    private String followingUrl;
    private String gistsUrl;
    private String starredUrl;
    private String subscriptionsUrl;
    private String organizationsUrl;
    private String reposUrl;
    private String eventsUrl;
    private String receivedEventsUrl;
    private String type;
    private Boolean siteAdmin;

    public static User fromJson(JSONObject jsonObject) {
        return new User()
                .setUrl(jsonObject.getString(USER_URL_FIELD))
                .setHtmlUrl(jsonObject.getString(USER_HTML_URL_FIELD))
                .setId(jsonObject.getInt(USER_ID_FIELD))
                .setLogin(jsonObject.getString(USER_LOGIN_FIELD));
    }
}
