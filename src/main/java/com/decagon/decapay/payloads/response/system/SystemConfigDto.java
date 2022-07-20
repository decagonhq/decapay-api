package com.decagon.decapay.payloads.response.system;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude
public class SystemConfigDto {
    private String name;
    private String value;
    private String key;
    private String description;
    private String type;
    private ConfigMetadata metadata;
}
