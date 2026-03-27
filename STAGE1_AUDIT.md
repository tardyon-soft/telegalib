# Stage 1 Audit Report

Date: 2026-03-26

Scope:
- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`
- `telegram-bot-framework-demo`

Reference docs:
- https://core.telegram.org/bots/api
- https://core.telegram.org/bots/api-changelog

## Confirmed By Docs

### Bot API methods
- `getMe`: implemented with no request parameters.
- `getUpdates`: implemented with `offset`, `limit`, `timeout`, `allowed_updates`.
- `sendMessage`: implemented with required `chat_id` and `text`.
- `editMessageText`: implemented with union result model (`Message` or `True`).
- `deleteMessage`: modeled as `boolean` success.
- `answerCallbackQuery`: modeled as `boolean` success; supports empty answer and answer with text.

### Response envelope and errors
- Envelope fields mapped: `ok`, `result`, `description`, `error_code`, `parameters`.
- API error behavior (`ok=false`) mapped to `TelegramApiException` with `errorCode`, `description`, `rawBody`.

### DTO semantics checked
- `Update.update_id` is used for long polling offset advancement.
- `CallbackQuery.message` is modeled as `MaybeInaccessibleMessage` (`Message` or `InaccessibleMessage`).
- `InaccessibleMessage.date` semantics handled via documented `date=0` discriminator.
- `Chat.type` is used by chat-type filters (`private/group/supergroup/channel`).
- Stage 1 DTO optional fields are nullable and not forced as required.

### Long polling semantics
- Sequential processing.
- `offset` advances only after successful update handling.
- Advancement logic uses highest processed `update_id + 1`.

## Simplified Intentionally

- No webhook runtime implementation.
- No automatic webhook-state verification before `getUpdates` calls.
- No command parser extensions (`/cmd@botname`, deep-link parser).
- Minimal resilience model for polling errors (simple backoff).
- Stage 1 DTO subset only.

## Deferred To Future Stages

- Webhook mode and webhook lifecycle.
- Extended Telegram methods (media upload and multipart).
- Advanced retry/rate-limit/circuit-breaker policy.
- Richer DTO coverage and advanced command parsing.

## Module Boundaries Confirmed

- Core:
  - No Spring dependencies in build or runtime API.
  - Contains Stage 1 runtime logic (API client, polling, dispatcher/router/filters, wrappers).
- Starter:
  - Depends on core.
  - Provides only wiring/autoconfiguration/properties/lifecycle.
  - Does not reimplement core runtime logic.
- Demo:
  - Depends on starter.
  - Provides only example router/handlers and config.
  - No library runtime classes implemented in demo.

## Open Questions / Gaps

- `getUpdates` and webhook mutual exclusivity is documented; current Stage 1 runtime does not preflight webhook state and relies on API error response.
- `allowed_updates` validation is delegated to Telegram API (no strict local validation list in Stage 1).

## Verification Commands

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
GRADLE_USER_HOME=/tmp/telegalib-gradle-home \
./gradlew --no-daemon \
  :telegram-bot-framework-core:test \
  :telegram-bot-framework-spring-boot-starter:test \
  :telegram-bot-framework-demo:compileJava
```
