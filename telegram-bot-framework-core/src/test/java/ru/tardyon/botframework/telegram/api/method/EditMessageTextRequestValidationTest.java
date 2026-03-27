package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EditMessageTextRequestValidationTest {

    @Test
    void allowsChatAndMessageTarget() {
        assertDoesNotThrow(() -> new EditMessageTextRequest(123L, 5, null, "text"));
    }

    @Test
    void allowsInlineTarget() {
        assertDoesNotThrow(() -> new EditMessageTextRequest(null, null, "inline-id", "text"));
    }

    @Test
    void rejectsMixedTargets() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new EditMessageTextRequest(123L, 5, "inline-id", "text")
        );
    }

    @Test
    void rejectsPartialChatTarget() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new EditMessageTextRequest(123L, null, null, "text")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new EditMessageTextRequest(null, 5, null, "text")
        );
    }
}
