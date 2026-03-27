package ru.tardyon.botframework.telegram.api.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Resolves capabilities from a declared Bot API version or a manual profile.
 * No network-based auto-detection is performed.
 */
public final class BotApiCapabilitiesResolver {

    private BotApiCapabilitiesResolver() {
    }

    public static BotApiCapabilities forDeclaredVersion(BotApiVersion version) {
        Objects.requireNonNull(version, "version must not be null");

        EnumSet<BotApiCapability> resolved = EnumSet.noneOf(BotApiCapability.class);
        for (BotApiCapability capability : BotApiCapability.values()) {
            BotApiVersion introducedIn = BotApiCapabilityRegistry.introducedIn(capability);
            if (version.compareTo(introducedIn) >= 0) {
                resolved.add(capability);
            }
        }
        return new ImmutableBotApiCapabilities(version, resolved);
    }

    public static BotApiCapabilities forManualProfile(BotApiVersion declaredVersion, Collection<BotApiCapability> capabilities) {
        Objects.requireNonNull(declaredVersion, "declaredVersion must not be null");
        Objects.requireNonNull(capabilities, "capabilities must not be null");

        EnumSet<BotApiCapability> manual = capabilities.isEmpty()
            ? EnumSet.noneOf(BotApiCapability.class)
            : EnumSet.copyOf(capabilities);

        return new ImmutableBotApiCapabilities(declaredVersion, manual);
    }

    private record ImmutableBotApiCapabilities(
        BotApiVersion declaredVersion,
        Set<BotApiCapability> supportedCapabilities
    ) implements BotApiCapabilities {

        private ImmutableBotApiCapabilities {
            supportedCapabilities = supportedCapabilities.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(supportedCapabilities));
        }

        @Override
        public boolean supports(BotApiCapability capability) {
            return supportedCapabilities.contains(capability);
        }
    }
}
