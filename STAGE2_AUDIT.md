# Stage 2 Audit Report

Date: 2026-03-27  
Source of truth: [Telegram Bot API](https://core.telegram.org/bots/api), optional [Bot API changelog](https://core.telegram.org/bots/api-changelog)

## Confirmed By Docs

- HTTP model and envelope:
  - `https://api.telegram.org/bot<token>/METHOD_NAME`
  - response envelope fields `ok`, `result`, `description`, `error_code`, optional `parameters`  
  Reference: [Making requests](https://core.telegram.org/bots/api#making-requests)
- Webhook methods:
  - `setWebhook`, `deleteWebhook`, `getWebhookInfo`  
  Reference: [setWebhook](https://core.telegram.org/bots/api#setwebhook), [deleteWebhook](https://core.telegram.org/bots/api#deletewebhook), [getWebhookInfo](https://core.telegram.org/bots/api#getwebhookinfo)
- Reply markup editing:
  - `editMessageReplyMarkup` modeled as `Message | True` result wrapper  
  Reference: [editMessageReplyMarkup](https://core.telegram.org/bots/api#editmessagereplymarkup)
- Commands API:
  - `setMyCommands`, `getMyCommands`, `BotCommand`, `BotCommandScope*`  
  Reference: [setMyCommands](https://core.telegram.org/bots/api#setmycommands), [getMyCommands](https://core.telegram.org/bots/api#getmycommands), [BotCommand](https://core.telegram.org/bots/api#botcommand), [BotCommandScope](https://core.telegram.org/bots/api#botcommandscope)
- Files API:
  - `getFile`, `File` DTO mapping (`TelegramFile`), download URL flow `/file/bot<token>/<file_path>`, `sendDocument` with JSON reference and multipart upload paths  
  Reference: [getFile](https://core.telegram.org/bots/api#getfile), [File](https://core.telegram.org/bots/api#file), [sendDocument](https://core.telegram.org/bots/api#senddocument), [Sending files](https://core.telegram.org/bots/api#sending-files)
- Webhook secret token handling:
  - header name `X-Telegram-Bot-Api-Secret-Token` passed into core webhook processor and validated there  
  Reference: [setWebhook](https://core.telegram.org/bots/api#setwebhook)
- Update delivery exclusivity:
  - `getUpdates` and webhook are mutually exclusive by Bot API semantics; runtime mode guard exists in `DefaultTelegramBot`  
  Reference: [Getting updates](https://core.telegram.org/bots/api#getting-updates)

## Simplified Intentionally (Stage 2)

- `sendDocument` supports minimal subset needed for Stage 2:
  - `chat_id`, `document`, `caption`, `reply_markup`
  - no full optional parameter surface.
- Keyboard model uses minimal button fields for MVP ergonomics:
  - inline: `text`, `url`, `callback_data`
  - reply: `text`.
- Starter webhook integration is minimal servlet endpoint + lifecycle wiring (no platform-specific production hardening).
- Command parsing is practical (`/cmd`, `/cmd@bot`, args raw) without full command framework.

## Deferred To Future Stages

- FSM/stateful conversation framework.
- Inline query mode.
- Payments/business APIs.
- Media groups and full media object hierarchy.
- Advanced reliability policies (complex retry/rate-limiting/circuit-breakers).
- Production deployment patterns for webhook infrastructure.
- Annotation-driven handler scanning DSL in starter.

## Module Boundaries Confirmed

- `telegram-bot-framework-core`
  - No Spring dependencies.
  - Contains Stage 2 runtime/library logic (API client, webhook processor, dispatcher/middleware, keyboards, commands, files).
- `telegram-bot-framework-spring-boot-starter`
  - Depends on `core`.
  - Provides wiring/autoconfiguration/properties/lifecycle/webhook endpoint integration.
  - Does not duplicate core dispatcher/filter/runtime logic.
- `telegram-bot-framework-demo`
  - Depends on starter.
  - Provides usage scenarios only (handlers/config examples).
  - No library runtime implementation moved into demo.

## Stage 1 Compatibility Check

- Stage 1 methods and flows remain present and covered by existing tests.
- Full multi-module test suite passes (`./gradlew test`) after Stage 2 additions.
- Polling behavior and Stage 1 dispatcher/filter/wrapper tests continue to pass.

## Open Questions / Gaps

- Core currently does not enforce all documented value constraints (e.g., full validation of `secret_token` character set/length).  
  Status: acceptable for Stage 2; documented as non-blocking gap.
- Starter webhook lifecycle does not auto-call `deleteWebhook` on shutdown.  
  Status: intentional safety simplification; may be revisited with explicit policy in Stage 3.
