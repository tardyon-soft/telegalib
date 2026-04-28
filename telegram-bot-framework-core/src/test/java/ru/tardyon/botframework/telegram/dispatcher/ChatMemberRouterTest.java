package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMemberAdministrator;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMemberLeft;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMemberUpdated;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext.UpdateType;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class ChatMemberRouterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesMyChatMemberUpdate() throws Exception {
        String json = """
            {
              "update_id": 100,
              "my_chat_member": {
                "chat": {"id": -1001, "type": "channel", "title": "News"},
                "from": {"id": 10, "is_bot": false, "first_name": "Admin"},
                "date": 1710000000,
                "old_chat_member": {
                  "status": "left",
                  "user": {"id": 20, "is_bot": true, "first_name": "Bot", "username": "sample_bot"}
                },
                "new_chat_member": {
                  "status": "administrator",
                  "user": {"id": 20, "is_bot": true, "first_name": "Bot", "username": "sample_bot"},
                  "can_post_messages": true
                }
              }
            }
            """;

        Update update = objectMapper.readValue(json, Update.class);
        UpdateContext context = new UpdateContext(update);

        assertEquals(UpdateType.MY_CHAT_MEMBER, context.getUpdateType());
        assertEquals("channel", context.getMyChatMember().chat().type());
        assertEquals("left", context.getMyChatMember().oldChatMember().status());
        assertEquals("administrator", context.getMyChatMember().newChatMember().status());
    }

    @Test
    void routesMyChatMemberAndChatMemberUpdates() {
        Router router = new Router();
        AtomicInteger myChatMemberHits = new AtomicInteger();
        AtomicInteger chatMemberHits = new AtomicInteger();

        router.myChatMember(Filters.any(), (ctx, event) -> myChatMemberHits.incrementAndGet());
        router.chatMember(Filters.any(), (ctx, event) -> chatMemberHits.incrementAndGet());

        router.route(new UpdateContext(updateWithMyChatMember()));
        router.route(new UpdateContext(updateWithChatMember()));

        assertEquals(1, myChatMemberHits.get());
        assertEquals(1, chatMemberHits.get());
    }

    private static Update updateWithMyChatMember() {
        return new Update(
            1L,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            chatMemberUpdated(),
            null
        );
    }

    private static Update updateWithChatMember() {
        return new Update(
            2L,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            chatMemberUpdated()
        );
    }

    private static ChatMemberUpdated chatMemberUpdated() {
        User bot = new User(20L, true, "Bot", null, "sample_bot", null, null, null, null);
        return new ChatMemberUpdated(
            new Chat(-1001L, "channel", "News", "news", null, null, null),
            new User(10L, false, "Admin", null, "admin", null, null, null, null),
            1_710_000_000L,
            new ChatMemberLeft("left", bot),
            new ChatMemberAdministrator("administrator", bot, null, null, null),
            null,
            null,
            null
        );
    }
}
