package ru.tardyon.botframework.telegram.generator.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import ru.tardyon.botframework.telegram.generator.model.ApiFieldSpec;
import ru.tardyon.botframework.telegram.generator.model.ApiMethodSpec;
import ru.tardyon.botframework.telegram.generator.model.ApiSchemaModel;
import ru.tardyon.botframework.telegram.generator.model.ApiTypeSpec;
import ru.tardyon.botframework.telegram.generator.model.GenerationReport;

public final class JavaSubsetCodeWriter {

    public GenerationReport write(ApiSchemaModel schemaModel, Path outputDirectory) {
        Objects.requireNonNull(schemaModel, "schemaModel must not be null");
        Objects.requireNonNull(outputDirectory, "outputDirectory must not be null");

        List<Path> generated = new ArrayList<>();
        try {
            Files.createDirectories(outputDirectory);

            for (ApiTypeSpec typeSpec : schemaModel.types().stream().sorted(Comparator.comparing(ApiTypeSpec::name)).toList()) {
                Path file = writeType(typeSpec, outputDirectory, schemaModel);
                generated.add(file);
            }
            for (ApiMethodSpec methodSpec : schemaModel.methods().stream().sorted(Comparator.comparing(ApiMethodSpec::requestClassName)).toList()) {
                Path file = writeMethodRequest(methodSpec, outputDirectory, schemaModel);
                generated.add(file);
            }
            Path catalogFile = writeCatalog(schemaModel, outputDirectory);
            generated.add(catalogFile);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write generated code", e);
        }

        return new GenerationReport(outputDirectory, List.copyOf(generated));
    }

    private Path writeType(ApiTypeSpec typeSpec, Path outputDirectory, ApiSchemaModel schemaModel) throws IOException {
        StringBuilder code = new StringBuilder();
        code.append("package ").append(typeSpec.packageName()).append(";\n\n");
        code.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n");
        code.append("import com.fasterxml.jackson.annotation.JsonProperty;\n\n");
        code.append("/**\n");
        code.append(" * GENERATED FROM TELEGRAM BOT API SUBSET SCHEMA.\n");
        code.append(" * Source: ").append(schemaModel.source()).append("\n");
        code.append(" * Schema version: ").append(schemaModel.schemaVersion()).append("\n");
        code.append(" */\n");
        code.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
        code.append("public record ").append(typeSpec.name()).append("(\n");
        code.append(joinFields(typeSpec.fields()));
        code.append("\n) {\n}\n");

        return writeJavaFile(outputDirectory, typeSpec.packageName(), typeSpec.name(), code.toString());
    }

    private Path writeMethodRequest(ApiMethodSpec methodSpec, Path outputDirectory, ApiSchemaModel schemaModel) throws IOException {
        StringBuilder code = new StringBuilder();
        code.append("package ").append(methodSpec.requestPackage()).append(";\n\n");
        code.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
        code.append("import java.util.Objects;\n\n");
        code.append("/**\n");
        code.append(" * GENERATED FROM TELEGRAM BOT API SUBSET SCHEMA.\n");
        code.append(" * Method: ").append(methodSpec.methodName()).append("\n");
        code.append(" * Source: ").append(schemaModel.source()).append("\n");
        code.append(" */\n");
        code.append("public record ").append(methodSpec.requestClassName()).append("(\n");
        code.append(joinFields(methodSpec.parameters()));
        code.append("\n) {\n");
        if (methodSpec.parameters().stream().anyMatch(ApiFieldSpec::required)) {
            code.append("    public ").append(methodSpec.requestClassName()).append(" {\n");
            for (ApiFieldSpec parameter : methodSpec.parameters()) {
                if (parameter.required()) {
                    code.append("        Objects.requireNonNull(").append(parameter.name()).append(", \"")
                        .append(parameter.name()).append(" must not be null\");\n");
                }
            }
            code.append("    }\n");
        }
        code.append("}\n");

        return writeJavaFile(outputDirectory, methodSpec.requestPackage(), methodSpec.requestClassName(), code.toString());
    }

    private Path writeCatalog(ApiSchemaModel schemaModel, Path outputDirectory) throws IOException {
        String packageName = "ru.tardyon.botframework.telegram.generator.generated";
        String className = "GeneratedBotApiMethodCatalog";

        StringBuilder code = new StringBuilder();
        code.append("package ").append(packageName).append(";\n\n");
        code.append("import java.util.List;\n\n");
        code.append("/**\n");
        code.append(" * GENERATED FROM TELEGRAM BOT API SUBSET SCHEMA.\n");
        code.append(" * Contains generated method->request/result mapping for deterministic review.\n");
        code.append(" */\n");
        code.append("public final class ").append(className).append(" {\n\n");
        code.append("    private ").append(className).append("() {\n    }\n\n");
        code.append("    public record MethodSpec(String methodName, String requestType, String resultType) {\n    }\n\n");
        code.append("    public static final List<MethodSpec> METHODS = List.of(\n");

        List<ApiMethodSpec> sorted = schemaModel.methods().stream()
            .sorted(Comparator.comparing(ApiMethodSpec::methodName))
            .toList();

        for (int i = 0; i < sorted.size(); i++) {
            ApiMethodSpec spec = sorted.get(i);
            code.append("        new MethodSpec(\"")
                .append(spec.methodName()).append("\", \"")
                .append(spec.requestPackage()).append('.').append(spec.requestClassName()).append("\", \"")
                .append(spec.resultType()).append("\")");
            if (i < sorted.size() - 1) {
                code.append(',');
            }
            code.append("\n");
        }

        code.append("    );\n");
        code.append("}\n");

        return writeJavaFile(outputDirectory, packageName, className, code.toString());
    }

    private static String joinFields(List<ApiFieldSpec> fields) {
        return fields.stream()
            .map(field -> "    @JsonProperty(\"" + field.jsonName() + "\") " + field.javaType() + " " + field.name())
            .collect(Collectors.joining(",\n"));
    }

    private static Path writeJavaFile(Path outputDirectory, String packageName, String className, String code) throws IOException {
        String relative = packageName.replace('.', '/') + "/" + className + ".java";
        Path file = outputDirectory.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, normalizeNewlines(code), StandardCharsets.UTF_8);
        return file;
    }

    private static String normalizeNewlines(String raw) {
        return raw.replace("\r\n", "\n").replace("\r", "\n");
    }
}
