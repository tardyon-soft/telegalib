package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticErrorEvent;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingFinishedEvent;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingStartedEvent;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class DefaultDispatcherDiagnosticsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void emitsUpdateStartAndFinishEvents() throws Exception {
        List<UpdateProcessingStartedEvent> started = new ArrayList<>();
        List<UpdateProcessingFinishedEvent> finished = new ArrayList<>();

        DiagnosticsHooks hooks = DiagnosticsHooks.builder()
            .addUpdateProcessingListener(new ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingListener() {
                @Override
                public void onUpdateStarted(UpdateProcessingStartedEvent event) {
                    started.add(event);
                }

                @Override
                public void onUpdateFinished(UpdateProcessingFinishedEvent event) {
                    finished.add(event);
                }
            })
            .build();

        Router router = new Router();
        router.message(Filters.any(), (ctx, message) -> {
        });
        DefaultDispatcher dispatcher = new DefaultDispatcher(router, List.of(), hooks);

        UpdateContext context = new UpdateContext(parseUpdateWithMessage(101L, "ping"));
        dispatcher.dispatch(context);

        assertEquals(1, started.size());
        assertEquals(1, finished.size());
        assertEquals(101L, started.getFirst().updateId());
        assertEquals("DISPATCH", started.getFirst().source());
        assertEquals(started.getFirst().correlationId(), finished.getFirst().correlationId());
        assertTrue(finished.getFirst().success());
        assertTrue(finished.getFirst().durationMillis() >= 0);
    }

    @Test
    void emitsErrorEventWhenHandlerFails() throws Exception {
        List<DiagnosticErrorEvent> errors = new ArrayList<>();

        DiagnosticsHooks hooks = DiagnosticsHooks.builder()
            .addErrorListener(errors::add)
            .build();

        Router router = new Router();
        router.message(Filters.any(), (ctx, message) -> {
            throw new IllegalStateException("boom");
        });
        DefaultDispatcher dispatcher = new DefaultDispatcher(router, List.of(), hooks);

        UpdateContext context = new UpdateContext(parseUpdateWithMessage(102L, "ping"));
        assertThrows(IllegalStateException.class, () -> dispatcher.dispatch(context));

        assertEquals(1, errors.size());
        DiagnosticErrorEvent event = errors.getFirst();
        assertEquals("dispatcher", event.component());
        assertEquals("dispatch", event.operation());
        assertEquals(102L, event.updateId());
        assertNotNull(event.error());
        assertFalse(event.error().getMessage().isBlank());
    }

    private ru.tardyon.botframework.telegram.api.model.Update parseUpdateWithMessage(long updateId, String text) throws Exception {
        String json = """
            {
              "update_id": %d,
              "message": {
                "message_id": 1,
                "date": 1710000000,
                "chat": {"id": 123, "type": "private"},
                "text": "%s"
              }
            }
            """.formatted(updateId, text);
        return objectMapper.readValue(json, ru.tardyon.botframework.telegram.api.model.Update.class);
    }
}
