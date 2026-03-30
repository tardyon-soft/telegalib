package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonDeserialize(using = ChatMember.Deserializer.class)
public sealed interface ChatMember
    permits ChatMemberOwner,
    ChatMemberAdministrator,
    ChatMemberMember,
    ChatMemberRestricted,
    ChatMemberLeft,
    ChatMemberBanned,
    ChatMemberUnknown {

    String status();

    User user();

    final class Deserializer extends JsonDeserializer<ChatMember> {
        @Override
        public ChatMember deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            JsonNode statusNode = node.get("status");
            if (statusNode == null || !statusNode.isTextual()) {
                return new ChatMemberUnknown(null, null, node);
            }
            String status = statusNode.asText();
            User user = node.hasNonNull("user") ? parser.getCodec().treeToValue(node.get("user"), User.class) : null;
            return switch (status) {
                case "creator" -> new ChatMemberOwner(
                    status,
                    user,
                    node.path("is_anonymous").asBoolean(false),
                    textOrNull(node.get("custom_title"))
                );
                case "administrator" -> new ChatMemberAdministrator(
                    status,
                    user,
                    boolOrNull(node.get("can_be_edited")),
                    boolOrNull(node.get("is_anonymous")),
                    textOrNull(node.get("custom_title"))
                );
                case "member" -> new ChatMemberMember(status, user, textOrNull(node.get("tag")));
                case "restricted" -> new ChatMemberRestricted(
                    status,
                    user,
                    boolOrNull(node.get("is_member")),
                    longOrNull(node.get("until_date")),
                    textOrNull(node.get("tag"))
                );
                case "left" -> new ChatMemberLeft(status, user);
                case "kicked" -> new ChatMemberBanned(status, user, longOrNull(node.get("until_date")));
                default -> new ChatMemberUnknown(status, user, node);
            };
        }

        private static Boolean boolOrNull(JsonNode node) {
            if (node == null || node.isNull()) {
                return null;
            }
            return node.asBoolean();
        }

        private static Long longOrNull(JsonNode node) {
            if (node == null || node.isNull()) {
                return null;
            }
            return node.asLong();
        }

        private static String textOrNull(JsonNode node) {
            if (node == null || node.isNull() || !node.isTextual()) {
                return null;
            }
            return node.asText();
        }
    }
}
