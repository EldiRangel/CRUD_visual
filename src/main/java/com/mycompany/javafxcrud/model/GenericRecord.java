package com.mycompany.javafxcrud.model;

import java.util.HashMap;
import java.util.Map;

public class GenericRecord {
    private Map<String, Object> values = new HashMap<>();
    
    public void setValue(String columnName, Object value) {
        values.put(columnName.toLowerCase(), value);
    }
    
    public Object getValue(String columnName) {
        return values.get(columnName.toLowerCase());
    }
    
    public Map<String, Object> getAllValues() {
        return new HashMap<>(values);
    }
}