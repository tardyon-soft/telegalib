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

    @Test
    void sendGiftValidatesTargetAndTextLength() {
        assertDoesNotThrow(() -> new SendGiftRequest(1L, null, "gift-1", null, "ok", null, null));
        assertDoesNotThrow(() -> new SendGiftRequest(null, "@channel", "gift-1", true, null, null, null));

        IllegalArgumentException noTarget = assertThrows(
            IllegalArgumentException.class,
            () -> new SendGiftRequest(null, null, "gift-1", null, null, null, null)
        );
        assertEquals("Either userId or chatId must be specified", noTarget.getMessage());

        IllegalArgumentException textLength = assertThrows(
            IllegalArgumentException.class,
            () -> new SendGiftRequest(1L, null, "gift-1", null, "x".repeat(129), null, null)
        );
        assertEquals("text length must be in range 0..128", textLength.getMessage());
    }

    @Test
    void giftPremiumSubscriptionValidatesMonthAndStars() {
        assertDoesNotThrow(() -> new GiftPremiumSubscriptionRequest(1L, 3, 1000, null, null, null));
        assertDoesNotThrow(() -> new GiftPremiumSubscriptionRequest(1L, 6, 1500, null, null, null));
        assertDoesNotThrow(() -> new GiftPremiumSubscriptionRequest(1L, 12, 2500, null, null, null));

        IllegalArgumentException badMonth = assertThrows(
            IllegalArgumentException.class,
            () -> new GiftPremiumSubscriptionRequest(1L, 4, 1000, null, null, null)
        );
        assertEquals("monthCount must be one of 3, 6, or 12", badMonth.getMessage());

        IllegalArgumentException badStars = assertThrows(
            IllegalArgumentException.class,
            () -> new GiftPremiumSubscriptionRequest(1L, 3, 999, null, null, null)
        );
        assertEquals("starCount does not match monthCount requirements", badStars.getMessage());
    }

    @Test
    void giftsPaginationRequestsValidateLimits() {
        assertDoesNotThrow(() -> new GetUserGiftsRequest(1L, null, null, null, null, null, null, null, 100));
        assertDoesNotThrow(() -> new GetChatGiftsRequest(1L, null, null, null, null, null, null, null, null, null, 1));

        IllegalArgumentException userLimit = assertThrows(
            IllegalArgumentException.class,
            () -> new GetUserGiftsRequest(1L, null, null, null, null, null, null, null, 101)
        );
        assertEquals("limit must be in range 1..100", userLimit.getMessage());

        IllegalArgumentException chatLimit = assertThrows(
            IllegalArgumentException.class,
            () -> new GetChatGiftsRequest(1L, null, null, null, null, null, null, null, null, null, 0)
        );
        assertEquals("limit must be in range 1..100", chatLimit.getMessage());
    }

    @Test
    void chatSubscriptionInviteRequestsValidateBounds() {
        assertDoesNotThrow(() -> new CreateChatSubscriptionInviteLinkRequest("@channel", "Pro", 2592000, 1));
        assertDoesNotThrow(() -> new EditChatSubscriptionInviteLinkRequest("@channel", "https://t.me/+abc", "Pro+"));

        IllegalArgumentException periodEx = assertThrows(
            IllegalArgumentException.class,
            () -> new CreateChatSubscriptionInviteLinkRequest("@channel", "Pro", 3600, 10)
        );
        assertEquals("subscriptionPeriod must be 2592000", periodEx.getMessage());

        IllegalArgumentException priceEx = assertThrows(
            IllegalArgumentException.class,
            () -> new CreateChatSubscriptionInviteLinkRequest("@channel", "Pro", 2592000, 0)
        );
        assertEquals("subscriptionPrice must be in range 1..10000", priceEx.getMessage());
    }
}
