package ru.tardyon.botframework.telegram.api.model.menu;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

class MenuButtonSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesWebAppMenuButtonInSetChatMenuButtonRequest() throws Exception {
        SetChatMenuButtonRequest request = new SetChatMenuButtonRequest(
            123L,
            MenuButtonWebApp.of("Open app", new WebAppInfo("https://example.com/app"))
        );

        String json = objectMapper.writeValueAsString(request);
        assertTrue(json.contains("\"chat_id\":123"));
        assertTrue(json.contains("\"menu_button\":{\"type\":\"web_app\""));
        assertTrue(json.contains("\"web_app\":{\"url\":\"https://example.com/app\"}"));
    }

    @Test
    void deserializesMenuButtonByType() throws Exception {
        MenuButton commands = objectMapper.readValue("""
            {"type":"commands"}
            """, MenuButton.class);
        MenuButton webApp = objectMapper.readValue("""
            {"type":"web_app","text":"Open","web_app":{"url":"https://example.com/a"}}
            """, MenuButton.class);

        assertInstanceOf(MenuButtonCommands.class, commands);
        assertInstanceOf(MenuButtonWebApp.class, webApp);
    }
}
