package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;

@JsonDeserialize(using = EditMessageResult.EditMessageResultDeserializer.class)
public final class EditMessageResult {

    private final Message message;
    private final boolean successful;

    public EditMessageResult(Message message, boolean successful) {
        this.message = message;
        this.successful = successful;
    }

    public Message getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    static final class EditMessageResultDeserializer extends JsonDeserializer<EditMessageResult> {

        @Override
        public EditMessageResult deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            if (node.isBoolean()) {
                return new EditMessageResult(null, node.booleanValue());
            }
            if (node.isObject()) {
                Message message = codec.treeToValue(node, Message.class);
                return new EditMessageResult(message, true);
            }
            return context.reportInputMismatch(
                EditMessageResult.class,
                "edit message result must be Message object or boolean"
            );
        }
    }
}
