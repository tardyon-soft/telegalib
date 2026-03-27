package ru.tardyon.botframework.telegram.generator.model;

import java.util.List;
import java.util.Objects;

public record ApiSchemaModel(
    String schemaVersion,
    String source,
    List<ApiTypeSpec> types,
    List<ApiMethodSpec> methods
) {

    public ApiSchemaModel {
        schemaVersion = requireText(schemaVersion, "schemaVersion");
        source = requireText(source, "source");
        types = List.copyOf(Objects.requireNonNull(types, "types must not be null"));
        methods = List.copyOf(Objects.requireNonNull(methods, "methods must not be null"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
