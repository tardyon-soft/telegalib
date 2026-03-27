# Telegram Bot Framework (Stage 5)

Multi-module Gradle project for Telegram bot runtime/library with Stage 5 scope.

## Modules Overview

- `telegram-bot-framework-core`
  - Vanilla Java runtime/library (no Spring dependencies).
  - Telegram Bot API client, DTO/model layer, polling/webhook runtimes, dispatcher/router/filters, middleware, FSM/state, inline mode, keyboards, commands, files/media, payments/webapp/business, and Stage 5 monetization/business operations.
- `telegram-bot-framework-spring-boot-starter`
  - Thin Spring Boot adapter over `core`.
  - Auto-configuration, properties binding, polling/webhook lifecycle, webhook endpoint integration, middleware collection, annotation-driven handler registration, and Stage 5 helper bean wiring.
- `telegram-bot-framework-demo`
  - Spring Boot sample app using starter.
  - Demonstrates Stage 5 scenarios (paid media, stars, gifts, subscriptions, business story/checklist/gifts).
- `telegram-bot-framework-botapi-generator`
  - Stage 6 tooling module for Bot API code generation pipeline.
  - Not a production runtime dependency for application modules.
- `telegram-bot-framework-testkit`
  - Stage 6 testing/support module for fake Bot API server, fixtures, assertions, and update simulation.
  - Not a production runtime dependency for application modules.

## Supported In Stage 5

- Core Bot API methods from Stage 1-4 plus Stage 5 monetization/business operations:
  - monetization: `sendPaidMedia`, `getMyStarBalance`, `getStarTransactions`, `refundStarPayment`, `editUserStarSubscription`
  - gifts/subscriptions: `getAvailableGifts`, `sendGift`, `giftPremiumSubscription`, `getUserGifts`, `getChatGifts`, `createChatSubscriptionInviteLink`, `editChatSubscriptionInviteLink`
  - business advanced: `postStory`, `editStory`, `deleteStory`, `repostStory`, `sendChecklist`, `editMessageChecklist`, `setBusinessAccountGiftSettings`, `getBusinessAccountStarBalance`, `transferBusinessAccountStars`, `getBusinessAccountGifts`, `convertGiftToStars`, `upgradeGift`, `transferGift`
- DTO/model coverage for Stage 5 objects in current scope:
  - paid media hierarchy, stars transactions, gifts/owned gifts, story/input story content, checklist/input checklist, accepted gift types
- Starter:
  - annotation filters for Stage 5 service-message fields (`paid_media`, `gift`, `unique_gift`, `refunded_payment`, etc.)
  - argument resolution for Stage 5 DTOs
  - thin helper beans: `TelegramMonetizationOperations`, `TelegramBusinessOperations`
  - manual `Router` wiring still supported
- Demo:
  - Stage 5 command scenarios and service-message handlers
- Stage 6 scaffolding:
  - `botapi-generator` package skeleton for parser/model/writer pipeline
  - `testkit` package skeleton for fake server/fixture/assertion/update simulation

## Not Supported Yet

- MTProto-only features and non-Bot-API surfaces.
- Production billing/reconciliation engines and payout/accounting systems.
- Full gifts/treasury/backoffice suite beyond current Stage 5 methods.
- Full WebApp frontend SDK wrapper stack.
- Distributed storage or infra-heavy operational components as mandatory part of library.
- Code generation and fake Telegram server/testkit.

## Design Constraints

- Source of truth for Telegram semantics:
  - https://core.telegram.org/bots/api
  - https://core.telegram.org/bots/inline
  - https://core.telegram.org/bots/webapps
  - https://core.telegram.org/bots/payments
  - https://core.telegram.org/bots/payments-stars
  - https://core.telegram.org/bots/api-changelog
- Project coordinates and package baseline:
  - groupId: `ru.tardyon.botframework`
  - base package: `ru.tardyon.botframework.telegram`
- `core` must remain Spring-free and usable standalone.
- `starter` must remain a thin integration layer and must not duplicate core runtime/business logic.
- `demo` is usage sample only and must not become runtime implementation.
- Scope is stage-bounded; avoid speculative abstractions and out-of-stage features.

## Capability Layer (Stage 6 Scaffold)

Core contains a version/capability compatibility model in package
`ru.tardyon.botframework.telegram.api.capability`.

Example usage:

```java
import ru.tardyon.botframework.telegram.api.capability.BotApiCapabilities;
import ru.tardyon.botframework.telegram.api.capability.BotApiCapabilitiesResolver;
import ru.tardyon.botframework.telegram.api.capability.BotApiCapability;
import ru.tardyon.botframework.telegram.api.capability.BotApiVersion;

BotApiCapabilities caps = BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.of(9, 3));
boolean paidMediaSupported = caps.supports(BotApiCapability.PAID_MEDIA);
boolean privateTopicsSupported = caps.supports(BotApiCapability.PRIVATE_CHAT_TOPICS);
```

The layer is explicit and manual:
- no network auto-detection
- no runtime permission engine
- only declared version or manually configured capability profile

## Transport Profiles (Stage 6 Scaffold)

Core transport profile types:
- `ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportMode`
  - `CLOUD`
  - `LOCAL`
- `ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile`

Cloud mode example:

```java
var client = new DefaultTelegramApiClient(
    System.getenv("BOT_TOKEN"),
    BotApiTransportProfile.cloudDefault()
);
```

Local Bot API mode example:

```java
var client = new DefaultTelegramApiClient(
    System.getenv("BOT_TOKEN"),
    BotApiTransportProfile.local("http://127.0.0.1:8081")
);
```

Local mode path upload example (compatible with Bot API local server `file://` support):

```java
client.sendDocument(SendDocumentRequest.of(123L, InputFile.path(Path.of("/absolute/path/report.pdf"))));
```

Notes:
- Default mode remains cloud (`https://api.telegram.org`).
- Local mode is transport/profile support only; runtime API remains the same.
- According to official Bot API local server notes, local mode can use:
  - local file paths via `file://` URI semantics for uploads,
  - webhook over HTTP,
  - local IP addresses and arbitrary ports for webhook endpoint.
