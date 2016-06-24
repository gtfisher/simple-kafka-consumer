package com.russmiles.antifragile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

class Event {
    private final String eventType;

    public Map<String, Object> getParameters() {
        return parameters;
    }



    private final Map<String, Object> parameters;



    Event(String eventType, LinkedHashMap<String, Object> stringStringLinkedHashMap) {
        this.eventType = eventType;
        parameters = stringStringLinkedHashMap;
    }


//    @JsonCreator
//    Event (@JsonProperty("eventType") String eventType, @JsonProperty("eventData") Object eventData) {
//       this.eventType =eventType;
//       this.eventData = eventData;
//    }



    public String getEventType() {
        return eventType;
    }


}
