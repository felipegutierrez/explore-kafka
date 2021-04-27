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
public class Label {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private Integer id;
    private String url;
    private String name;
    private String color;
    private Boolean _default;
}
