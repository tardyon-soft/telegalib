package ru.tardyon.botframework.telegram.spring.boot.service;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.EditStoryRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest;
import ru.tardyon.botframework.telegram.api.method.PostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.RepostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.SendChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.SetBusinessAccountGiftSettingsRequest;
import ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest;
import ru.tardyon.botframework.telegram.api.method.TransferGiftRequest;
import ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.story.Story;

public class TelegramBusinessOperations {

    private final TelegramApiClient telegramApiClient;

    public TelegramBusinessOperations(TelegramApiClient telegramApiClient) {
        this.telegramApiClient = Objects.requireNonNull(telegramApiClient, "telegramApiClient must not be null");
    }

    public BusinessConnection getBusinessConnection(GetBusinessConnectionRequest request) {
        return telegramApiClient.getBusinessConnection(request);
    }

    public boolean readBusinessMessage(ReadBusinessMessageRequest request) {
        return telegramApiClient.readBusinessMessage(request);
    }

    public boolean deleteBusinessMessages(DeleteBusinessMessagesRequest request) {
        return telegramApiClient.deleteBusinessMessages(request);
    }

    public Story postStory(PostStoryRequest request) {
        return telegramApiClient.postStory(request);
    }

    public Story editStory(EditStoryRequest request) {
        return telegramApiClient.editStory(request);
    }

    public boolean deleteStory(DeleteStoryRequest request) {
        return telegramApiClient.deleteStory(request);
    }

    public Story repostStory(RepostStoryRequest request) {
        return telegramApiClient.repostStory(request);
    }

    public Message sendChecklist(SendChecklistRequest request) {
        return telegramApiClient.sendChecklist(request);
    }

    public Message editMessageChecklist(EditMessageChecklistRequest request) {
        return telegramApiClient.editMessageChecklist(request);
    }

    public boolean setBusinessAccountGiftSettings(SetBusinessAccountGiftSettingsRequest request) {
        return telegramApiClient.setBusinessAccountGiftSettings(request);
    }

    public StarAmount getBusinessAccountStarBalance(GetBusinessAccountStarBalanceRequest request) {
        return telegramApiClient.getBusinessAccountStarBalance(request);
    }

    public boolean transferBusinessAccountStars(TransferBusinessAccountStarsRequest request) {
        return telegramApiClient.transferBusinessAccountStars(request);
    }

    public OwnedGifts getBusinessAccountGifts(GetBusinessAccountGiftsRequest request) {
        return telegramApiClient.getBusinessAccountGifts(request);
    }

    public boolean convertGiftToStars(ConvertGiftToStarsRequest request) {
        return telegramApiClient.convertGiftToStars(request);
    }

    public boolean upgradeGift(UpgradeGiftRequest request) {
        return telegramApiClient.upgradeGift(request);
    }

    public boolean transferGift(TransferGiftRequest request) {
        return telegramApiClient.transferGift(request);
    }
}
