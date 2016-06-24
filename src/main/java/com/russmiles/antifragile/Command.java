package com.russmiles.antifragile;

import java.util.LinkedHashMap;
import java.util.Map;

class Command {
    private final String commandType;
    private final Map<String, Object> parameters;
    Command(String s, LinkedHashMap<String, Object> stringStringLinkedHashMap) {
        commandType = s;
        parameters = stringStringLinkedHashMap;
    }

    public String getCommandType() {
        return commandType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
