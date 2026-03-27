package ru.tardyon.botframework.telegram.generator.model;

import java.util.List;
import java.util.Objects;

public record ApiMethodSpec(
    String methodName,
    String requestClassName,
    String requestPackage,
    String resultType,
    String description,
    List<ApiFieldSpec> parameters
) {

    public ApiMethodSpec {
        methodName = requireText(methodName, "methodName");
        requestClassName = requireText(requestClassName, "requestClassName");
        requestPackage = requireText(requestPackage, "requestPackage");
        resultType = requireText(resultType, "resultType");
        parameters = List.copyOf(Objects.requireNonNull(parameters, "parameters must not be null"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
