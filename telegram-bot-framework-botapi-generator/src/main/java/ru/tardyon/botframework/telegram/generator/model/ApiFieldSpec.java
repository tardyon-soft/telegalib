package ru.tardyon.botframework.telegram.generator.model;

import java.util.Objects;

public record ApiFieldSpec(
    String name,
    String javaType,
    String jsonName,
    boolean required,
    String description
) {

    public ApiFieldSpec {
        name = requireText(name, "name");
        javaType = requireText(javaType, "javaType");
        jsonName = requireText(jsonName, "jsonName");
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
