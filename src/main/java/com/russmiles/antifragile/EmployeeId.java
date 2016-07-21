package com.russmiles.antifragile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by gtarrant-fisher on 22/06/2016.
 */
public class EmployeeId {

    private final int id;

    public EmployeeId (Map<String,Object> objectMap){
        this((Integer) objectMap.get("id"));
    }


    public EmployeeId(@JsonProperty("id") int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeId)) return false;

        EmployeeId that = (EmployeeId) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
