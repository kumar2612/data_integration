package com.example.cameldynamicpipeline.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlowFile {
    private final String id;
    private final long createdTime;
    private Object content;
    private final Map<String, Object> attributes;

    public FlowFile() {
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}