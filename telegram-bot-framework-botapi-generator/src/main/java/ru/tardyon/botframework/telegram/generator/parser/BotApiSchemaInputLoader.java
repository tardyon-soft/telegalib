package ru.tardyon.botframework.telegram.generator.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class BotApiSchemaInputLoader {

    private BotApiSchemaInputLoader() {
    }

    public static String loadFromPath(Path path) {
        Objects.requireNonNull(path, "path must not be null");
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read schema from path: " + path, e);
        }
    }

    public static String loadFromClasspath(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        try (InputStream inputStream = BotApiSchemaInputLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read classpath resource: " + resourcePath, e);
        }
    }
}
