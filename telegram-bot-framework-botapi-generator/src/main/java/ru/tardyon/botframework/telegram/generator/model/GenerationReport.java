package ru.tardyon.botframework.telegram.generator.model;

import java.nio.file.Path;
import java.util.List;

public record GenerationReport(
    Path outputDirectory,
    List<Path> generatedFiles
) {
}
