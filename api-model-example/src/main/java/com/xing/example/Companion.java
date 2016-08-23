package com.xing.example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Companion {
    private Set<String> fields = new LinkedHashSet<>();

    public String buildFields() {
        return build("");
    }

    String buildSubFields(String parentName) {
        if (parentName == null) {
            throw new IllegalArgumentException("parentName should not be null");
        }
        return build(parentName);
    }

    private String build(String prefix) {
        StringBuilder builder = new StringBuilder();

        for(Iterator<String> it = fields.iterator(); it.hasNext();) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            if (prefix.isEmpty()) {
                builder.append(it.next());
            } else {
                builder.append(String.format("%s.%s", prefix, it.next()));
            }
        }
        return builder.toString();
    }

    protected void addField(String fieldName) {
        fields.add(fieldName);
//        if (fieldsBuilder.length() > 0) {
//            fieldsBuilder.append(',');
//        }
//        fieldsBuilder.append(fieldName);
    }
}
