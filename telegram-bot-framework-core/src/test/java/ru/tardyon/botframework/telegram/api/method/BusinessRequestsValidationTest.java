package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class BusinessRequestsValidationTest {

    @Test
    void deleteBusinessMessagesRequiresNonEmptyMessageIds() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new DeleteBusinessMessagesRequest("bc-1", List.of())
        );
        assertEquals("messageIds must not be empty", ex.getMessage());
    }

    @Test
    void getBusinessConnectionRequiresNonBlankId() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new GetBusinessConnectionRequest(" ")
        );
        assertEquals("businessConnectionId must not be blank", ex.getMessage());
    }
}
