package ru.tardyon.botframework.telegram.generator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.generator.model.ApiSchemaModel;

class BotApiSubsetSchemaParserTest {

    @Test
    void parsesSubsetSchemaFromClasspathResource() {
        String raw = BotApiSchemaInputLoader.loadFromClasspath("/botapi/subset-schema.json");
        ApiSchemaModel model = new BotApiSubsetSchemaParser().parse(raw);

        assertEquals("1.0", model.schemaVersion());
        assertEquals(3, model.types().size());
        assertEquals(3, model.methods().size());
        assertFalse(model.types().getFirst().fields().isEmpty());
        assertFalse(model.methods().getFirst().parameters().isEmpty());
    }
}
