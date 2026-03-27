# Telegram Bot Framework (Stage 2)

Multi-module Gradle project for Telegram bot runtime/library with Stage 2 scope.

## Modules Overview

- `telegram-bot-framework-core`
  - Vanilla Java runtime/library (no Spring).
  - Telegram Bot API client, DTO, polling + webhook processing, dispatcher/router/filters, middleware, keyboards, commands API, file API.
- `telegram-bot-framework-spring-boot-starter`
  - Thin Spring Boot adapter over `core`.
  - Properties binding, auto-configuration, polling/webhook mode wiring, webhook endpoint integration, lifecycle orchestration.
- `telegram-bot-framework-demo`
  - Spring Boot sample app using starter + core API.
  - Demonstrates commands, keyboards, callbacks, middleware, and file flow examples.

## Supported In Stage 2

- Telegram methods:
  - `getMe`, `getUpdates`, `sendMessage`, `editMessageText`, `deleteMessage`, `answerCallbackQuery`
  - `setWebhook`, `deleteWebhook`, `getWebhookInfo`
  - `setMyCommands`, `getMyCommands`
  - `editMessageReplyMarkup`
  - `getFile`, `sendDocument`
- Polling runtime:
  - long polling with offset advancement and graceful stop.
- Webhook runtime:
  - raw JSON webhook update processing into dispatcher flow.
  - secret token validation support.
- Dispatcher/runtime:
  - router + filters + handlers for message/callback updates.
  - update middleware chain (including short-circuit support).
- Keyboards:
  - inline keyboard + reply keyboard minimal DTO/builders.
- Commands:
  - basic command parsing with optional `@botusername` and args.
- Files:
  - file metadata retrieval, file download helpers, basic multipart upload for `sendDocument`.

## Not Supported Yet

- FSM/state machine framework.
- Inline query mode.
- Payments/business APIs.
- Media groups and full media hierarchy.
- Advanced production concerns:
  - distributed offset storage
  - advanced retry/rate-limit/circuit-breaker strategies
  - production deployment automation for webhook infrastructure.
- Annotation-driven handler scanning DSL in starter.

## Design Constraints

- Source of truth for Telegram semantics: official docs only:
  - https://core.telegram.org/bots/api
  - https://core.telegram.org/bots/api-changelog
- `core` must remain usable without Spring.
- `starter` must stay a thin integration layer and must not duplicate core runtime logic.
- `demo` is usage example only, not a runtime implementation module.
- Keep scope minimal and predictable per stage; avoid speculative abstractions.
