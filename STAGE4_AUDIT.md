# Stage 4 Audit Report

Date: 2026-03-27  
Scope: verification-only (no feature expansion)

## Confirmed By Docs

### Core methods and update types

Verified against official docs:

- `sendInvoice`
- `answerShippingQuery`
- `answerPreCheckoutQuery`
- `answerWebAppQuery`
- `savePreparedInlineMessage`
- `getBusinessConnection`
- `readBusinessMessage`
- `deleteBusinessMessages`
- update fields:
  - `shipping_query`
  - `pre_checkout_query`
  - `business_connection`
  - `business_message`
  - `edited_business_message`
  - `deleted_business_messages`
  - `web_app_data`

Code points:

- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/TelegramApiClient.java`
- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/DefaultTelegramApiClient.java`
- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/model/Update.java`
- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/model/Message.java`

### DTO mapping

Verified DTOs/fields are aligned with Stage 4 target:

- Payment:
  - `ShippingQuery`
  - `PreCheckoutQuery`
  - `SuccessfulPayment`
- Web Apps:
  - `WebAppInfo`
  - `WebAppData`
  - `SentWebAppMessage`
  - `PreparedInlineMessage`
- Business:
  - `BusinessConnection` (`user_chat_id` uses `Long`)
  - `BusinessMessagesDeleted`

Code points:

- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/model/payment/*`
- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/model/webapp/*`
- `telegram-bot-framework-core/src/main/java/ru/tardyon/botframework/telegram/api/model/business/*`

### Semantics

- Pre-checkout timing note is explicitly preserved:
  - `TelegramApiClient.answerPreCheckoutQuery(...)` JavaDoc contains 10-second note.
- Invoice/Stars behavior:
  - Generic invoice flow remains supported.
  - Stars-specific constraints are documented in demo and supported by API shape (`currency="XTR"`, empty `provider_token` allowed).
- initData validation:
  - HMAC SHA-256 algorithm with `WebAppData` constant key derivation is implemented.
  - `auth_date` is exposed and optional max-age staleness check exists.
- Web App data handling:
  - `Message.web_app_data` mapped and routable through normal message path.
  - Starter annotation layer supports `@OnMessage(webAppDataPresent = true)` and `WebAppData` method argument.
- Business semantics:
  - business update types are routed in core router.
  - business methods are present in low-level client and available in wrappers.

## Simplified Intentionally

- No local permission engine for `BusinessBotRights`; rights are documented/modeled only.
- No automatic scheduler/timeout enforcement for the 10-second pre-checkout response window (note is documented).
- No mandatory Ed25519 third-party signature validator helper for Mini Apps (optional in docs; HMAC path implemented).
- No full Stage 4 production billing orchestration (retries/idempotency/business process layer).

## Deferred To Future Stages

- Payments advanced flows beyond Stage 4 minimum (full refund/subscription orchestration, dispute tooling).
- Extended business admin surface and treasury/gifts/stars account operations.
- Full Mini App frontend SDK abstractions and UI-side helper ecosystem.
- Additional Telegram method coverage introduced after current Stage 4 scope.

## Module Boundaries Confirmed

- `core`
  - contains Stage 4 runtime/library logic
  - no Spring dependencies in `src/main/java`
- `starter`
  - depends on `core`
  - provides wiring/autoconfiguration/lifecycle/annotation integration only
  - does not duplicate core dispatcher/runtime logic
- `demo`
  - depends on `starter`
  - demonstrates usage only
  - does not contain reusable library runtime implementation

Build/dependency checks:

- group: `ru.tardyon.botframework`
- projects: `telegram-bot-framework-core`, `telegram-bot-framework-spring-boot-starter`, `telegram-bot-framework-demo`

## Open Questions / Gaps

- Telegram Bot API evolves quickly (current docs show Bot API 9.5). Stage 4 audit confirms implemented subset only; non-covered new methods/fields are intentionally out of scope.
- For digital goods in Stars mode, operational safeguards (e.g. product catalog/state consistency, anti-duplication business logic) remain app-level responsibilities and are not framework-enforced.

## Verification Runs

- `./gradlew --no-daemon clean test` — success on all modules.
- `./gradlew --no-daemon projects` — module topology confirmed.

## Sources

- https://core.telegram.org/bots/api
- https://core.telegram.org/bots/webapps
- https://core.telegram.org/bots/payments
- https://core.telegram.org/bots/payments-stars
- https://core.telegram.org/bots/api-changelog
