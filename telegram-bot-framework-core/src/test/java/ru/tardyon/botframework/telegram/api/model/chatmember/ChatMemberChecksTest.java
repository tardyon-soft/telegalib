package ru.tardyon.botframework.telegram.api.model.chatmember;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.User;

class ChatMemberChecksTest {

    private static final User USER = new User(1L, false, "U", null, null, null, null, null, null);

    @Test
    void subscribedStatusesAreDetected() {
        assertTrue(ChatMemberChecks.isSubscribed(new ChatMemberOwner("creator", USER, false, null)));
        assertTrue(ChatMemberChecks.isSubscribed(new ChatMemberAdministrator("administrator", USER, null, null, null)));
        assertTrue(ChatMemberChecks.isSubscribed(new ChatMemberMember("member", USER, null)));
        assertTrue(ChatMemberChecks.isSubscribed(new ChatMemberRestricted("restricted", USER, true, null, null)));
        assertFalse(ChatMemberChecks.isSubscribed(new ChatMemberRestricted("restricted", USER, false, null, null)));
        assertFalse(ChatMemberChecks.isSubscribed(new ChatMemberLeft("left", USER)));
        assertFalse(ChatMemberChecks.isSubscribed(new ChatMemberBanned("kicked", USER, null)));
    }

    @Test
    void adminAndOwnerChecksWork() {
        assertTrue(ChatMemberChecks.isOwner(new ChatMemberOwner("creator", USER, false, null)));
        assertFalse(ChatMemberChecks.isOwner(new ChatMemberMember("member", USER, null)));

        assertTrue(ChatMemberChecks.isAdministrator(new ChatMemberAdministrator("administrator", USER, null, null, null)));
        assertFalse(ChatMemberChecks.isAdministrator(new ChatMemberLeft("left", USER)));

        assertTrue(ChatMemberChecks.isAdminOrOwner(new ChatMemberOwner("creator", USER, false, null)));
        assertTrue(ChatMemberChecks.isAdminOrOwner(new ChatMemberAdministrator("administrator", USER, null, null, null)));
        assertFalse(ChatMemberChecks.isAdminOrOwner(new ChatMemberMember("member", USER, null)));
    }
}
