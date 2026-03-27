package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * Telegram union type for callback_query.message: Message or InaccessibleMessage.
 */
public sealed interface MaybeInaccessibleMessage permits Message, InaccessibleMessage {

    final class MaybeInaccessibleMessageDeserializer extends JsonDeserializer<MaybeInaccessibleMessage> {

        @Override
        public MaybeInaccessibleMessage deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            if (!node.isObject()) {
                return context.reportInputMismatch(
                    MaybeInaccessibleMessage.class,
                    "MaybeInaccessibleMessage must be a JSON object"
                );
            }

            JsonNode dateNode = node.get("date");
            if (dateNode != null && dateNode.isIntegralNumber() && dateNode.intValue() == 0) {
                return codec.treeToValue(node, InaccessibleMessage.class);
            }
            return codec.treeToValue(node, Message.class);
        }
    }
}
