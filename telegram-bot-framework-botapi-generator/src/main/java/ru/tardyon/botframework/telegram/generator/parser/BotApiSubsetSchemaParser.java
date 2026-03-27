package ru.tardyon.botframework.telegram.generator.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.generator.model.ApiFieldSpec;
import ru.tardyon.botframework.telegram.generator.model.ApiMethodSpec;
import ru.tardyon.botframework.telegram.generator.model.ApiSchemaModel;
import ru.tardyon.botframework.telegram.generator.model.ApiTypeSpec;

public final class BotApiSubsetSchemaParser {

    private final ObjectMapper objectMapper;

    public BotApiSubsetSchemaParser() {
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ApiSchemaModel parse(String rawSchemaJson) {
        Objects.requireNonNull(rawSchemaJson, "rawSchemaJson must not be null");
        try {
            RawSchema raw = objectMapper.readValue(rawSchemaJson, RawSchema.class);
            List<ApiTypeSpec> types = raw.types().stream()
                .map(this::toType)
                .toList();
            List<ApiMethodSpec> methods = raw.methods().stream()
                .map(this::toMethod)
                .toList();
            return new ApiSchemaModel(raw.schemaVersion(), raw.source(), types, methods);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse generator schema", e);
        }
    }

    private ApiTypeSpec toType(RawType rawType) {
        return new ApiTypeSpec(
            rawType.name(),
            rawType.packageName(),
            rawType.description(),
            rawType.fields().stream().map(this::toField).toList()
        );
    }

    private ApiMethodSpec toMethod(RawMethod rawMethod) {
        return new ApiMethodSpec(
            rawMethod.methodName(),
            rawMethod.requestClassName(),
            rawMethod.requestPackage(),
            rawMethod.resultType(),
            rawMethod.description(),
            rawMethod.parameters().stream().map(this::toField).toList()
        );
    }

    private ApiFieldSpec toField(RawField rawField) {
        return new ApiFieldSpec(
            rawField.name(),
            rawField.javaType(),
            rawField.jsonName(),
            rawField.required(),
            rawField.description()
        );
    }

    private record RawSchema(
        String schemaVersion,
        String source,
        List<RawType> types,
        List<RawMethod> methods
    ) {
    }

    private record RawType(
        String name,
        String packageName,
        String description,
        List<RawField> fields
    ) {
    }

    private record RawMethod(
        String methodName,
        String requestClassName,
        String requestPackage,
        String resultType,
        String description,
        List<RawField> parameters
    ) {
    }

    private record RawField(
        String name,
        String javaType,
        String jsonName,
        boolean required,
        String description
    ) {
    }
}
