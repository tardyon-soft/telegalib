package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
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
}
