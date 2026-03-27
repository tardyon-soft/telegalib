package ru.tardyon.botframework.telegram.api.model.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.method.GetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;

class BotCommandScopeSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesDefaultScopeInSetMyCommands() throws Exception {
        SetMyCommandsRequest request = new SetMyCommandsRequest(
            java.util.List.of(new BotCommand("start", "Start")),
            new BotCommandScopeDefault(),
            null
        );

        String json = objectMapper.writeValueAsString(request);
        assertTrue(json.contains("\"scope\":{\"type\":\"default\"}"));
    }

    @Test
    void serializesChatAndChatMemberScopesInGetMyCommands() throws Exception {
        GetMyCommandsRequest chatScopeRequest = new GetMyCommandsRequest(new BotCommandScopeChat(123L), null);
        GetMyCommandsRequest memberScopeRequest = new GetMyCommandsRequest(new BotCommandScopeChatMember(123L, 777L), null);

        String chatJson = objectMapper.writeValueAsString(chatScopeRequest);
        String memberJson = objectMapper.writeValueAsString(memberScopeRequest);

        assertTrue(chatJson.contains("\"scope\":{\"type\":\"chat\",\"chat_id\":123}"));
        assertTrue(memberJson.contains("\"scope\":{\"type\":\"chat_member\",\"chat_id\":123,\"user_id\":777}"));
    }
}
