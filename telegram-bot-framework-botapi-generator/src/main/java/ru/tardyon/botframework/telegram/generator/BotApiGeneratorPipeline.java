package ru.tardyon.botframework.telegram.generator;

import java.nio.file.Path;
import java.util.Objects;
import ru.tardyon.botframework.telegram.generator.model.ApiSchemaModel;
import ru.tardyon.botframework.telegram.generator.model.GenerationReport;
import ru.tardyon.botframework.telegram.generator.parser.BotApiSchemaInputLoader;
import ru.tardyon.botframework.telegram.generator.parser.BotApiSubsetSchemaParser;
import ru.tardyon.botframework.telegram.generator.writer.JavaSubsetCodeWriter;

/**
 * MVP generator pipeline: input acquisition -> parser -> intermediate model -> code writer.
 */
public final class BotApiGeneratorPipeline {

    private final BotApiSubsetSchemaParser parser;
    private final JavaSubsetCodeWriter writer;

    public BotApiGeneratorPipeline() {
        this(new BotApiSubsetSchemaParser(), new JavaSubsetCodeWriter());
    }

    public BotApiGeneratorPipeline(BotApiSubsetSchemaParser parser, JavaSubsetCodeWriter writer) {
        this.parser = Objects.requireNonNull(parser, "parser must not be null");
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
    }

    public GenerationReport generateFromClasspathResource(String resourcePath, Path outputDirectory) {
        String raw = BotApiSchemaInputLoader.loadFromClasspath(resourcePath);
        ApiSchemaModel model = parser.parse(raw);
        return writer.write(model, outputDirectory);
    }

    public GenerationReport generateFromFile(Path schemaPath, Path outputDirectory) {
        String raw = BotApiSchemaInputLoader.loadFromPath(schemaPath);
        ApiSchemaModel model = parser.parse(raw);
        return writer.write(model, outputDirectory);
    }
}
