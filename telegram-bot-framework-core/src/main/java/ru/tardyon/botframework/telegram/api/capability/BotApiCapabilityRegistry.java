package ru.tardyon.botframework.telegram.api.capability;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Explicit capability-to-version matrix based on Bot API changelog entries.
 */
public final class BotApiCapabilityRegistry {

    private static final Map<BotApiCapability, BotApiVersion> INTRODUCED_IN = buildMatrix();

    private BotApiCapabilityRegistry() {
    }

    public static Map<BotApiCapability, BotApiVersion> introducedInMatrix() {
        return INTRODUCED_IN;
    }

    public static BotApiVersion introducedIn(BotApiCapability capability) {
        BotApiVersion version = INTRODUCED_IN.get(capability);
        if (version == null) {
            throw new IllegalArgumentException("Unknown capability: " + capability);
        }
        return version;
    }

    private static Map<BotApiCapability, BotApiVersion> buildMatrix() {
        EnumMap<BotApiCapability, BotApiVersion> map = new EnumMap<>(BotApiCapability.class);

        // Bot API 2.0 changelog: inline bots / inline query support.
        map.put(BotApiCapability.INLINE_MODE, BotApiVersion.V2_0);

        // Bot API 6.0 changelog: Web Apps support.
        map.put(BotApiCapability.WEB_APPS, BotApiVersion.V6_0);

        // Bot API 3.0 changelog: payment platform introduction.
        map.put(BotApiCapability.PAYMENTS, BotApiVersion.V3_0);

        // Bot API 7.4 changelog: Telegram Stars payments/refunds.
        map.put(BotApiCapability.STARS, BotApiVersion.V7_4);

        // Bot API 7.6 changelog: sendPaidMedia + paid media objects.
        map.put(BotApiCapability.PAID_MEDIA, BotApiVersion.V7_6);

        // Bot API 8.0 changelog: getAvailableGifts/sendGift initial surface.
        map.put(BotApiCapability.GIFTS, BotApiVersion.V8_0);

        // Bot API 7.2 changelog: business_connection/business_message surface.
        map.put(BotApiCapability.BUSINESS_BASICS, BotApiVersion.V7_2);

        // Bot API 9.1 changelog: sendChecklist/editMessageChecklist (stories were added in 9.0).
        map.put(BotApiCapability.BUSINESS_STORIES_CHECKLISTS, BotApiVersion.V9_1);

        // Bot API 9.3 changelog: topics in private chats + message_thread_id support.
        map.put(BotApiCapability.PRIVATE_CHAT_TOPICS, BotApiVersion.V9_3);

        // sendMessageDraft introduced in 9.3, allowed for all bots in 9.5.
        map.put(BotApiCapability.SEND_MESSAGE_DRAFT, BotApiVersion.V9_5);

        return Collections.unmodifiableMap(map);
    }
}
