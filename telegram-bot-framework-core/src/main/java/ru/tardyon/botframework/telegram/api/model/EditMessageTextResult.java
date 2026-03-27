package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;

@JsonDeserialize(using = EditMessageTextResult.EditMessageTextResultDeserializer.class)
public final class EditMessageTextResult {

    private final Message message;
    private final boolean successful;

    public EditMessageTextResult(Message message, boolean successful) {
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

    static final class EditMessageTextResultDeserializer extends JsonDeserializer<EditMessageTextResult> {

        @Override
        public EditMessageTextResult deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            if (node.isBoolean()) {
                return new EditMessageTextResult(null, node.booleanValue());
            }
            if (node.isObject()) {
                Message message = codec.treeToValue(node, Message.class);
                return new EditMessageTextResult(message, true);
            }
            return context.reportInputMismatch(
                EditMessageTextResult.class,
                "editMessageText result must be Message object or boolean"
            );
        }
    }
}
