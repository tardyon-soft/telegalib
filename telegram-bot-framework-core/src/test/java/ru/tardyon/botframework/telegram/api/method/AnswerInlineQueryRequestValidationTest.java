package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;

class AnswerInlineQueryRequestValidationTest {

    @Test
    void allowsUpToFiftyResults() {
        List<InlineQueryResult> results = java.util.stream.IntStream.range(0, 50)
            .mapToObj(i -> new InlineQueryResultArticle("id-" + i, "title-" + i, InputTextMessageContent.of("text-" + i)))
            .map(result -> (InlineQueryResult) result)
            .toList();

        assertDoesNotThrow(() -> new AnswerInlineQueryRequest("iq-1", results, 10, true, null, null));
    }

    @Test
    void rejectsMoreThanFiftyResults() {
        List<InlineQueryResult> results = java.util.stream.IntStream.range(0, 51)
            .mapToObj(i -> new InlineQueryResultArticle("id-" + i, "title-" + i, InputTextMessageContent.of("text-" + i)))
            .map(result -> (InlineQueryResult) result)
            .toList();

        assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerInlineQueryRequest("iq-1", results, 10, true, null, null)
        );
    }

    @Test
    void rejectsNextOffsetLongerThan64Bytes() {
        String offset = "x".repeat(65);
        List<InlineQueryResult> results = List.of(
            new InlineQueryResultArticle("id-1", "title", InputTextMessageContent.of("text"))
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerInlineQueryRequest("iq-1", results, 10, true, offset, null)
        );
    }
}
