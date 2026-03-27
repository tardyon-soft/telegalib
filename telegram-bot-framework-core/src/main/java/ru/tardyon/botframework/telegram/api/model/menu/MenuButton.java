package ru.tardyon.botframework.telegram.api.model.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

@JsonDeserialize(using = MenuButton.MenuButtonDeserializer.class)
public sealed interface MenuButton permits MenuButtonDefault, MenuButtonCommands, MenuButtonWebApp {

    String type();

    final class MenuButtonDeserializer extends JsonDeserializer<MenuButton> {

        @Override
        public MenuButton deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            JsonNode typeNode = node.get("type");
            if (typeNode == null || !typeNode.isTextual()) {
                return context.reportInputMismatch(MenuButton.class, "MenuButton.type is required");
            }
            String type = typeNode.asText();
            return switch (type) {
                case "default" -> new MenuButtonDefault();
                case "commands" -> new MenuButtonCommands();
                case "web_app" -> {
                    JsonNode textNode = node.get("text");
                    JsonNode webAppNode = node.get("web_app");
                    if (textNode == null || !textNode.isTextual()) {
                        yield context.reportInputMismatch(MenuButton.class, "MenuButtonWebApp.text is required");
                    }
                    if (webAppNode == null || !webAppNode.isObject()) {
                        yield context.reportInputMismatch(MenuButton.class, "MenuButtonWebApp.web_app is required");
                    }
                    JsonNode urlNode = webAppNode.get("url");
                    if (urlNode == null || !urlNode.isTextual()) {
                        yield context.reportInputMismatch(MenuButton.class, "MenuButtonWebApp.web_app.url is required");
                    }
                    yield MenuButtonWebApp.of(textNode.asText(), new WebAppInfo(urlNode.asText()));
                }
                default -> context.reportInputMismatch(MenuButton.class, "Unsupported MenuButton.type: %s", type);
            };
        }
    }
}
