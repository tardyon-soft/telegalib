package ru.tardyon.botframework.telegram.generator.model;

import java.util.List;
import java.util.Objects;

public record ApiTypeSpec(
    String name,
    String packageName,
    String description,
    List<ApiFieldSpec> fields
) {

    public ApiTypeSpec {
        name = requireText(name, "name");
        packageName = requireText(packageName, "packageName");
        fields = List.copyOf(Objects.requireNonNull(fields, "fields must not be null"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
