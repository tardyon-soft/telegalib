package ru.tardyon.botframework.telegram.spring.boot.service;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.SendGiftRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.payment.Gifts;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.payment.StarTransactions;

public class TelegramMonetizationOperations {

    private final TelegramApiClient telegramApiClient;

    public TelegramMonetizationOperations(TelegramApiClient telegramApiClient) {
        this.telegramApiClient = Objects.requireNonNull(telegramApiClient, "telegramApiClient must not be null");
    }

    public Message sendPaidMedia(SendPaidMediaRequest request) {
        return telegramApiClient.sendPaidMedia(request);
    }

    public Gifts getAvailableGifts() {
        return telegramApiClient.getAvailableGifts();
    }

    public boolean sendGift(SendGiftRequest request) {
        return telegramApiClient.sendGift(request);
    }

    public boolean giftPremiumSubscription(GiftPremiumSubscriptionRequest request) {
        return telegramApiClient.giftPremiumSubscription(request);
    }

    public OwnedGifts getUserGifts(GetUserGiftsRequest request) {
        return telegramApiClient.getUserGifts(request);
    }

    public OwnedGifts getChatGifts(GetChatGiftsRequest request) {
        return telegramApiClient.getChatGifts(request);
    }

    public StarAmount getMyStarBalance() {
        return telegramApiClient.getMyStarBalance();
    }

    public StarTransactions getStarTransactions(GetStarTransactionsRequest request) {
        return telegramApiClient.getStarTransactions(request);
    }

    public boolean refundStarPayment(RefundStarPaymentRequest request) {
        return telegramApiClient.refundStarPayment(request);
    }

    public boolean editUserStarSubscription(EditUserStarSubscriptionRequest request) {
        return telegramApiClient.editUserStarSubscription(request);
    }
}
