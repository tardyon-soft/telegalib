package ru.tardyon.botframework.telegram.api.model.chatmember;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.GetChatMemberRequest;

public final class ChatMemberChecks {

    private ChatMemberChecks() {
    }

    public static boolean isOwner(ChatMember member) {
        return member != null && "creator".equals(member.status());
    }

    public static boolean isAdministrator(ChatMember member) {
        return member != null && "administrator".equals(member.status());
    }

    public static boolean isAdminOrOwner(ChatMember member) {
        return isOwner(member) || isAdministrator(member);
    }

    public static boolean isSubscribed(ChatMember member) {
        if (member == null || member.status() == null) {
            return false;
        }
        return switch (member.status()) {
            case "creator", "administrator", "member" -> true;
            case "restricted" -> member instanceof ChatMemberRestricted restricted && Boolean.TRUE.equals(restricted.isMember());
            default -> false;
        };
    }

    public static boolean isBotAdminOrOwner(TelegramApiClient apiClient, Object chatId) {
        Objects.requireNonNull(apiClient, "apiClient must not be null");
        Objects.requireNonNull(chatId, "chatId must not be null");
        long botId = apiClient.getMe().id();
        ChatMember member = apiClient.getChatMember(new GetChatMemberRequest(chatId, botId));
        return isAdminOrOwner(member);
    }
}
