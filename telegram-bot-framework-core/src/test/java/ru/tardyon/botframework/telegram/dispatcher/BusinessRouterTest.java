package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class BusinessRouterTest {

    @Test
    void routesBusinessUpdates() {
        Router router = new Router();
        AtomicInteger connectionHits = new AtomicInteger();
        AtomicInteger businessMessageHits = new AtomicInteger();
        AtomicInteger editedBusinessMessageHits = new AtomicInteger();
        AtomicInteger deletedBusinessHits = new AtomicInteger();

        router.businessConnection(Filters.any(), (ctx, connection) -> connectionHits.incrementAndGet());
        router.businessMessage(Filters.textEquals("hello"), (ctx, message) -> businessMessageHits.incrementAndGet());
        router.editedBusinessMessage(Filters.textStartsWith("edit"), (ctx, message) -> editedBusinessMessageHits.incrementAndGet());
        router.deletedBusinessMessages(Filters.any(), (ctx, deleted) -> deletedBusinessHits.incrementAndGet());

        BusinessConnection connection = new BusinessConnection(
            "bc-1",
            new User(1L, false, "Ann", null, "ann", "en", null, null, null),
            9001L,
            1,
            null,
            true
        );
        Message businessMessage = new Message(
            "bc-1",
            5,
            new User(1L, false, "Ann", null, "ann", "en", null, null, null),
            null,
            new Chat(100L, "private", null, null, "Ann", null, null),
            1,
            "hello",
            null,
            null,
            null
        );
        Message editedBusinessMessage = new Message(
            "bc-1",
            6,
            new User(1L, false, "Ann", null, "ann", "en", null, null, null),
            null,
            new Chat(100L, "private", null, null, "Ann", null, null),
            1,
            "edited",
            null,
            null,
            null
        );
        BusinessMessagesDeleted deleted = new BusinessMessagesDeleted(
            "bc-1",
            new Chat(100L, "private", null, null, "Ann", null, null),
            List.of(5, 6)
        );

        router.route(new UpdateContext(new Update(1L, null, null, null, null, null, null, null, connection, null, null, null, null, null)));
        router.route(new UpdateContext(new Update(2L, null, null, null, null, null, null, null, null, businessMessage, null, null, null, null)));
        router.route(new UpdateContext(new Update(3L, null, null, null, null, null, null, null, null, null, editedBusinessMessage, null, null, null)));
        router.route(new UpdateContext(new Update(4L, null, null, null, null, null, null, null, null, null, null, deleted, null, null)));

        assertEquals(1, connectionHits.get());
        assertEquals(1, businessMessageHits.get());
        assertEquals(1, editedBusinessMessageHits.get());
        assertEquals(1, deletedBusinessHits.get());
    }
}
