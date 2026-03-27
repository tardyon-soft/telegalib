package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.payment.LabeledPrice;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingOption;

class PaymentRequestsValidationTest {

    @Test
    void sendInvoiceRequiresPrices() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new SendInvoiceRequest(
                1L,
                "Title",
                "Description",
                "payload",
                "provider-token",
                "USD",
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );
    }

    @Test
    void answerShippingQueryValidatesOkVariants() {
        ShippingOption pickup = new ShippingOption("pickup", "Pickup", List.of(new LabeledPrice("Pickup", 0)));

        assertDoesNotThrow(() -> new AnswerShippingQueryRequest("sq-1", true, List.of(pickup), null));
        assertDoesNotThrow(() -> new AnswerShippingQueryRequest("sq-2", false, null, "Delivery is unavailable"));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerShippingQueryRequest("sq-3", true, null, null)
        );
        assertEquals("shippingOptions must not be empty when ok=true", ex.getMessage());
    }

    @Test
    void answerPreCheckoutQueryRequiresErrorWhenRejected() {
        assertDoesNotThrow(() -> new AnswerPreCheckoutQueryRequest("pcq-1", true, null));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerPreCheckoutQueryRequest("pcq-2", false, null)
        );
        assertEquals("errorMessage must not be blank when ok=false", ex.getMessage());
    }

    @Test
    void sendPaidMediaValidatesStarCountAndMediaSize() {
        InputPaidMediaPhoto photo = InputPaidMediaPhoto.of(InputFile.fileId("ph1"));
        assertDoesNotThrow(
            () -> new SendPaidMediaRequest(
                null,
                1L,
                1,
                List.of(photo),
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        IllegalArgumentException starCountEx = assertThrows(
            IllegalArgumentException.class,
            () -> new SendPaidMediaRequest(
                null,
                1L,
                0,
                List.of(photo),
                null,
                null,
                null,
                null,
                null,
                null
            )
        );
        assertEquals("starCount must be in range 1..25000", starCountEx.getMessage());

        IllegalArgumentException mediaSizeEx = assertThrows(
            IllegalArgumentException.class,
            () -> new SendPaidMediaRequest(
                null,
                1L,
                10,
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null
            )
        );
        assertEquals("media size must be in range 1..10", mediaSizeEx.getMessage());
    }

    @Test
    void getStarTransactionsRequestValidatesLimit() {
        assertDoesNotThrow(() -> new GetStarTransactionsRequest(null, null));
        assertDoesNotThrow(() -> new GetStarTransactionsRequest("next", 100));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new GetStarTransactionsRequest(null, 101)
        );
        assertEquals("limit must be in range 1..100", ex.getMessage());
    }

    @Test
    void refundAndSubscriptionRequestsValidateRequiredFields() {
        assertDoesNotThrow(() -> new RefundStarPaymentRequest(1L, "tg-charge-1"));
        assertDoesNotThrow(() -> new EditUserStarSubscriptionRequest(1L, "tg-sub-1", true));

        IllegalArgumentException refundEx = assertThrows(
            IllegalArgumentException.class,
            () -> new RefundStarPaymentRequest(1L, " ")
        );
        assertEquals("telegramPaymentChargeId must not be blank", refundEx.getMessage());

        IllegalArgumentException subscriptionEx = assertThrows(
            IllegalArgumentException.class,
            () -> new EditUserStarSubscriptionRequest(1L, " ", false)
        );
        assertEquals("telegramPaymentChargeId must not be blank", subscriptionEx.getMessage());
    }
}
