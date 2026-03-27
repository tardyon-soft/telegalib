package ru.tardyon.botframework.telegram.api.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportMode;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile;

class BotApiTransportProfileTest {

    @Test
    void cloudDefaultUsesOfficialBaseUrlAndCloudMode() {
        BotApiTransportProfile profile = BotApiTransportProfile.cloudDefault();

        assertEquals(BotApiTransportMode.CLOUD, profile.mode());
        assertEquals("https://api.telegram.org", profile.baseUrl());
        assertFalse(profile.localFileUriUploadEnabled());
    }

    @Test
    void localProfileEnablesLocalFileUriUploadByDefault() {
        BotApiTransportProfile profile = BotApiTransportProfile.local("http://127.0.0.1:8081");

        assertEquals(BotApiTransportMode.LOCAL, profile.mode());
        assertEquals("http://127.0.0.1:8081", profile.baseUrl());
        assertTrue(profile.localFileUriUploadEnabled());
    }
}
