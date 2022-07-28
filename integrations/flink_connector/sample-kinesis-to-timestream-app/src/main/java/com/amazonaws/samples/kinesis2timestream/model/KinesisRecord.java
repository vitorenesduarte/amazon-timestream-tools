package com.amazonaws.samples.kinesis2timestream.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KinesisRecord {
    @JsonProperty(value = "tableName", required = true)
    private String tableName;

    @JsonProperty(value = "dynamodb", required = true)
    private DynamoDB dynamodb;

    public Long getCreatedAt() {
        return this.getDynamodb().getEvent().getCreatedAt().getValue();
    }
    
    public String getEventType() {
        return this.getDynamodb().getEvent().getEventType().getValue();
    }

    public String getUser() {
        return this.getDynamodb().getEvent().getFields().getValue().getUser().getValue();
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class DynamoDB {
    @JsonProperty(value = "NewImage", required = true)
    private TeleportEvent event;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEvent {
    @JsonProperty(value = "CreatedAt", required = true)
    private TeleportEventCreatedAt createdAt;

    @JsonProperty(value = "EventType", required = true)
    private TeleportEventEventType eventType;

    @JsonProperty(value = "FieldsMap", required = true)
    private TeleportEventFieldsMap fields;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEventCreatedAt {
    @JsonProperty(value = "N", required = true)
    private Long value;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEventEventType {
    @JsonProperty(value = "S", required = true)
    private String value;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEventFieldsMap {
    @JsonProperty(value = "M", required = false)
    private TeleportEventFieldsMapValue value;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEventFieldsMapValue {
    @JsonProperty(value = "user", required = false)
    private TeleportEventFieldsMapUser user;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class TeleportEventFieldsMapUser {
    @JsonProperty(value = "S", required = false)
    private String value;
}