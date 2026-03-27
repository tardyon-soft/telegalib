# Telegram Bot Framework (Stage 6)

Multi-module Gradle project for Telegram bot runtime/library with Stage 6 scope.

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

## Supported In Stage 6

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
- Stage 6 platform/tooling maturity:
  - core transport profiles (`CLOUD`/`LOCAL`) and base URL override
  - core diagnostics hooks (`BotApiRequestListener`, `BotApiResponseListener`, `UpdateProcessingListener`, `ErrorListener`)
  - core capability/version model (`BotApiVersion`, `BotApiCapability`, resolver)
  - starter transport/diagnostics integration and capability bean exposure
  - generator MVP pipeline with deterministic generated subset output
  - testkit fake Bot API server + polling/webhook simulators + request assertions + fixtures
  - demo profiles for `cloud` / `local` / `fake` modes

## Not Supported Yet

- MTProto-only features and non-Bot-API surfaces.
- Production billing/reconciliation engines and payout/accounting systems.
- Full gifts/treasury/backoffice suite beyond current Stage 5 methods.
- Full WebApp frontend SDK wrapper stack.
- Distributed storage or infra-heavy operational components as mandatory part of library.
- Full protocol-accurate Telegram emulator in testkit.
- Generator auto-rewrite of handwritten runtime layers (dispatcher/FSM/starter) without manual review.

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

## Maven Central Publishing (core + starter)

The project is configured to publish only:
- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`

Required environment variables:
- `RELEASE_VERSION` (example: `0.2.0` or `0.2.1-SNAPSHOT`)
- `OSSRH_USERNAME`
- `OSSRH_PASSWORD`
- `SIGNING_KEY` (ASCII-armored private PGP key)
- `SIGNING_PASSWORD`

Publish commands:

```bash
export RELEASE_VERSION=0.2.0
export OSSRH_USERNAME=...
export OSSRH_PASSWORD=...
export SIGNING_KEY="$(cat ~/.gnupg/private.asc)"
export SIGNING_PASSWORD=...

./gradlew :telegram-bot-framework-core:publish
./gradlew :telegram-bot-framework-spring-boot-starter:publish
```

Notes:
- Snapshot versions (`*-SNAPSHOT`) go to Sonatype snapshots repository.
- Release versions go to Sonatype staging repository.

## Importing Library

Vanilla Java (core only):

```kotlin
implementation("ru.tardyon.botframework:telegram-bot-framework-core:<version>")
```

Spring Boot (starter, core comes transitively):

```kotlin
implementation("ru.tardyon.botframework:telegram-bot-framework-spring-boot-starter:<version>")
```

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

## Diagnostics Hooks (Stage 6 Scaffold)

Core diagnostics abstractions live in `ru.tardyon.botframework.telegram.diagnostics`:
- `BotApiRequestListener`
- `BotApiResponseListener`
- `UpdateProcessingListener`
- `ErrorListener`
- `DiagnosticsHooks` (registry/composer)
- `SensitiveDataRedactor` with default implementation `DefaultSensitiveDataRedactor`

Attach diagnostics listeners:

```java
DiagnosticsHooks hooks = DiagnosticsHooks.builder()
    .addRequestListener(event -> System.out.println("API request: " + event.methodName()))
    .addResponseListener(event -> System.out.println("API response ms: " + event.durationMillis()))
    .addErrorListener(event -> System.err.println("Error: " + event.component() + " " + event.operation()))
    .build();

DefaultTelegramApiClient client = new DefaultTelegramApiClient(
    token,
    BotApiTransportProfile.cloudDefault(),
    HttpClient.newHttpClient(),
    new ObjectMapper(),
    hooks
);
```

Log API timings:

```java
hooks = DiagnosticsHooks.builder()
    .addResponseListener(event ->
        System.out.println(event.methodName() + " took " + event.durationMillis() + "ms, success=" + event.success()))
    .build();
```

Redact payment/provider fields:

```java
String redacted = DefaultSensitiveDataRedactor.INSTANCE.redact(
    "{\"provider_token\":\"abc\",\"provider_data\":\"raw\",\"secret_token\":\"wh\"}"
);
// -> provider/token fields are replaced with <redacted>
```
