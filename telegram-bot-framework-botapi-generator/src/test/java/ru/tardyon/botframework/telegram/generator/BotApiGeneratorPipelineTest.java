package ru.tardyon.botframework.telegram.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.tardyon.botframework.telegram.generator.model.GenerationReport;

class BotApiGeneratorPipelineTest {

    @Test
    void generatesDeterministicSubsetFiles(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("generated");

        BotApiGeneratorPipeline pipeline = new BotApiGeneratorPipeline();
        GenerationReport first = pipeline.generateFromClasspathResource("/botapi/subset-schema.json", output);
        GenerationReport second = pipeline.generateFromClasspathResource("/botapi/subset-schema.json", output);

        assertEquals(first.generatedFiles().size(), second.generatedFiles().size());
        assertTrue(first.generatedFiles().size() >= 7);

        Path catalog = output.resolve("ru/tardyon/botframework/telegram/generator/generated/GeneratedBotApiMethodCatalog.java");
        assertTrue(Files.exists(catalog));

        String content = Files.readString(catalog);
        assertTrue(content.contains("GENERATED FROM TELEGRAM BOT API SUBSET SCHEMA"));
        assertTrue(content.contains("sendMessage"));
        assertTrue(content.contains("deleteMessage"));
    }
}
