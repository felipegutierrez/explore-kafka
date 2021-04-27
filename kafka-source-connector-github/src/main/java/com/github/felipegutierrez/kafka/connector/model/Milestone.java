package com.github.felipegutierrez.kafka.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Milestone {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private String url;
    private String htmlUrl;
    private String labelsUrl;
    private Integer id;
    private Integer number;
    private String state;
    private String title;
    private String description;
    private Creator creator;
    private Integer openIssues;
    private Integer closedIssues;
    private String createdAt;
    private String updatedAt;
    private String closedAt;
    private String dueOn;
}
