package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonDeserialize(using = TransactionPartner.Deserializer.class)
public sealed interface TransactionPartner
    permits TransactionPartnerUser,
    TransactionPartnerChat,
    TransactionPartnerFragment,
    TransactionPartnerTelegramAds,
    TransactionPartnerTelegramApi,
    TransactionPartnerOther,
    TransactionPartnerAffiliateProgram,
    TransactionPartnerUnknown {

    String type();

    final class Deserializer extends JsonDeserializer<TransactionPartner> {
        @Override
        public TransactionPartner deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            JsonNode typeNode = node.get("type");
            if (typeNode == null || !typeNode.isTextual()) {
                return new TransactionPartnerUnknown(null, node);
            }
            String type = typeNode.asText();
            return switch (type) {
                case "user" -> new TransactionPartnerUser(type, toUser(p, node.get("user")));
                case "chat" -> new TransactionPartnerChat(type, toChat(p, node.get("chat")));
                case "fragment" -> new TransactionPartnerFragment(
                    type,
                    node.hasNonNull("withdrawal_state")
                        ? p.getCodec().treeToValue(node.get("withdrawal_state"), RevenueWithdrawalState.class)
                        : null
                );
                case "telegram_ads" -> new TransactionPartnerTelegramAds(type);
                case "telegram_api" -> new TransactionPartnerTelegramApi(type);
                case "other" -> new TransactionPartnerOther(type);
                case "affiliate_program" -> new TransactionPartnerAffiliateProgram(type);
                default -> new TransactionPartnerUnknown(type, node);
            };
        }

        private static User toUser(JsonParser parser, JsonNode userNode) throws IOException {
            if (userNode == null || userNode.isNull()) {
                return null;
            }
            return parser.getCodec().treeToValue(userNode, User.class);
        }

        private static Chat toChat(JsonParser parser, JsonNode chatNode) throws IOException {
            if (chatNode == null || chatNode.isNull()) {
                return null;
            }
            return parser.getCodec().treeToValue(chatNode, Chat.class);
        }
    }
}
