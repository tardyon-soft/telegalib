package ru.tardyon.botframework.telegram.generator;

import java.nio.file.Path;
import ru.tardyon.botframework.telegram.generator.model.GenerationReport;

public final class BotApiCodegenMain {

    private static final String DEFAULT_SCHEMA_RESOURCE = "/botapi/subset-schema.json";

    private BotApiCodegenMain() {
    }

    public static void main(String[] args) {
        Arguments parsed = Arguments.parse(args);
        BotApiGeneratorPipeline pipeline = new BotApiGeneratorPipeline();

        GenerationReport report;
        if (parsed.schemaFile() != null) {
            report = pipeline.generateFromFile(parsed.schemaFile(), parsed.outputDirectory());
        } else {
            report = pipeline.generateFromClasspathResource(DEFAULT_SCHEMA_RESOURCE, parsed.outputDirectory());
        }

        System.out.println("Generated files: " + report.generatedFiles().size());
        for (Path file : report.generatedFiles()) {
            System.out.println(" - " + file);
        }
    }

    private record Arguments(Path outputDirectory, Path schemaFile) {

        private static Arguments parse(String[] args) {
            Path output = Path.of("build/generated/sources/botapi/java");
            Path schema = null;

            for (String arg : args) {
                if (arg.startsWith("--output=")) {
                    output = Path.of(arg.substring("--output=".length()));
                } else if (arg.startsWith("--schema-file=")) {
                    schema = Path.of(arg.substring("--schema-file=".length()));
                } else {
                    throw new IllegalArgumentException("Unknown argument: " + arg);
                }
            }
            return new Arguments(output, schema);
        }
    }
}
