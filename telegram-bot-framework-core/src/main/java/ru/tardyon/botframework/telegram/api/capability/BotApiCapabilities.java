package ru.tardyon.botframework.telegram.api.capability;

import java.util.Set;

/**
 * Resolved capability view for a specific declared profile.
 */
public interface BotApiCapabilities {

    BotApiVersion declaredVersion();

    boolean supports(BotApiCapability capability);

    Set<BotApiCapability> supportedCapabilities();
}
