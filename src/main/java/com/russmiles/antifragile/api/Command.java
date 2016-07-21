package com.russmiles.antifragile.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class Command {

    private final String commandType;
    private final Map<String, Object> parameters;

    public Command(@JsonProperty("commandType") String commandType, @JsonProperty("parameters")  Map<String, Object> map) {
        this.commandType = commandType;
        parameters = map;
    }

    public String getCommandType() {
        return commandType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
