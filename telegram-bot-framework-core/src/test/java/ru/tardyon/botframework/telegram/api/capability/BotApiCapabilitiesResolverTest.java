package ru.tardyon.botframework.telegram.api.capability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class BotApiCapabilitiesResolverTest {

    @Test
    void declaredVersionProfileSupportsPaidMediaOnlyFrom76() {
        BotApiCapabilities v75 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(7, 5));
        BotApiCapabilities v76 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(7, 6));

        assertFalse(v75.supports(BotApiCapability.PAID_MEDIA));
        assertTrue(v76.supports(BotApiCapability.PAID_MEDIA));
    }

    @Test
    void declaredVersionProfileSupportsPrivateChatTopicsFrom93() {
        BotApiCapabilities v92 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(9, 2));
        BotApiCapabilities v93 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(9, 3));

        assertFalse(v92.supports(BotApiCapability.PRIVATE_CHAT_TOPICS));
        assertTrue(v93.supports(BotApiCapability.PRIVATE_CHAT_TOPICS));
    }

    @Test
    void declaredVersionProfileTreatsSendMessageDraftAsGenerallyAvailableFrom95() {
        BotApiCapabilities v93 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(9, 3));
        BotApiCapabilities v95 = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(9, 5));

        assertFalse(v93.supports(BotApiCapability.SEND_MESSAGE_DRAFT));
        assertTrue(v95.supports(BotApiCapability.SEND_MESSAGE_DRAFT));
    }

    @Test
    void manualProfileUsesExplicitCapabilitiesOnly() {
        BotApiCapabilities manual = BotApiCapabilitiesResolver.forManualProfile(
            BotApiVersion.of(9, 5),
            Set.of(BotApiCapability.PAID_MEDIA)
        );

        assertTrue(manual.supports(BotApiCapability.PAID_MEDIA));
        assertFalse(manual.supports(BotApiCapability.PRIVATE_CHAT_TOPICS));
    }
}
