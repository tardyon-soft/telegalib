# Telegram Bot Framework (Stage 3)

Multi-module Gradle project for Telegram bot runtime/library with Stage 3 scope.

## Modules Overview

- `telegram-bot-framework-core`
  - Vanilla Java runtime/library (no Spring dependencies).
  - Telegram Bot API client, DTO/model layer, polling/webhook runtimes, dispatcher/router/filters, middleware, FSM/state, inline mode, keyboards, commands, menu button and files/media support.
- `telegram-bot-framework-spring-boot-starter`
  - Thin Spring Boot adapter over `core`.
  - Auto-configuration, properties binding, polling/webhook lifecycle, webhook endpoint integration, middleware collection, annotation-driven handler registration.
- `telegram-bot-framework-demo`
  - Spring Boot sample app using starter.
  - Demonstrates Stage 3 usage scenarios (FSM conversation, inline mode, callbacks, media group, menu button).

## Supported In Stage 3

- Core Bot API methods:
  - `getMe`, `getUpdates`, `sendMessage`, `editMessageText`, `deleteMessage`, `answerCallbackQuery`
  - `setWebhook`, `deleteWebhook`, `getWebhookInfo`
  - `answerInlineQuery`
  - `setMyCommands`, `getMyCommands`
  - `setChatMenuButton`, `getChatMenuButton`
  - `editMessageReplyMarkup`
  - `getFile`, `sendDocument`, `sendMediaGroup`
- Runtimes:
  - polling with offset advancement and graceful stop
  - webhook update processing with optional secret token validation
- Dispatcher/runtime:
  - routing for `message`, `edited_message`, `channel_post`, `edited_channel_post`, `callback_query`, `inline_query`, `chosen_inline_result`
  - composable filters and middleware chain
- FSM:
  - state abstraction, in-memory storage, state data, state filters
- Keyboards/UI:
  - inline/reply keyboards (Stage scope)
  - advanced inline button fields in current scope (`switch_inline_query*`, `copy_text`)
  - chat menu button API
- Files/media:
  - file metadata + download helpers
  - multipart upload for `sendDocument`
  - media groups for photo/video/document/audio with documented size/type validation
- Starter:
  - mode switch polling/webhook
  - manual `Router` wiring support
  - annotation-driven handlers: `@BotController`, `@OnMessage`, `@OnCallbackQuery`, `@OnInlineQuery`, `@OnChosenInlineResult`

## Not Supported Yet

- Payments APIs.
- Business APIs.
- Full Web App platform runtime.
- Full media hierarchy and media-group editing surface.
- Distributed state storages as built-in required implementations.
- Compile-time annotation processing/code generation.
- Production deployment automation/hardening for webhook infra.

## Design Constraints

- Source of truth for Telegram semantics:
  - https://core.telegram.org/bots/api
  - https://core.telegram.org/bots/inline
  - https://core.telegram.org/bots/api-changelog
- `core` must remain Spring-free and usable standalone.
- `starter` must remain a thin integration layer and must not duplicate core runtime/business logic.
- `demo` is usage sample only and must not become runtime implementation.
- Scope is stage-bounded; avoid speculative abstractions and out-of-stage features.
