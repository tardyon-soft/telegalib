package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;

class WebAppRequestsValidationTest {

    @Test
    void answerWebAppQueryRequestRejectsBlankId() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerWebAppQueryRequest(
                " ",
                new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("text"))
            )
        );
    }

    @Test
    void savePreparedInlineMessageRequestRejectsNullUserId() {
        assertThrows(
            NullPointerException.class,
            () -> new SavePreparedInlineMessageRequest(
                null,
                new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("text")),
                true,
                false,
                true,
                false
            )
        );
    }
}
