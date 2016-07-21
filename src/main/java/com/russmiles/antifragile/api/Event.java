package com.russmiles.antifragile.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Event {
    private final String eventType;

    private final Map<String, Object> parameters;

    public Event(@JsonProperty("eventType") String eventType, @JsonProperty("parameters") Map<String, Object> parametersMap) {
        this.eventType = eventType;
        parameters = parametersMap;
    }


    public  Map<String, Object> getParameters() {
        return parameters;
    }


    public String getEventType() {
        return eventType;
    }


}
