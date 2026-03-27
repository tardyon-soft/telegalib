# Telegram Bot Framework (Stage 4)

Multi-module Gradle project for Telegram bot runtime/library with Stage 4 scope.

## Modules Overview

- `telegram-bot-framework-core`
  - Vanilla Java runtime/library (no Spring dependencies).
  - Telegram Bot API client, DTO/model layer, polling/webhook runtimes, dispatcher/router/filters, middleware, FSM/state, inline mode, keyboards, commands, menu button, files/media and Stage 4 payment/webapp/business support.
- `telegram-bot-framework-spring-boot-starter`
  - Thin Spring Boot adapter over `core`.
  - Auto-configuration, properties binding, polling/webhook lifecycle, webhook endpoint integration, middleware collection, annotation-driven handler registration, Stage 4 handler types and Web App validator bean wiring.
- `telegram-bot-framework-demo`
  - Spring Boot sample app using starter.
  - Demonstrates Stage 4 usage scenarios (invoice/payment handlers, web_app_data, business updates) in addition to previous stages.

## Supported In Stage 4

- Core Bot API methods:
  - `getMe`, `getUpdates`, `sendMessage`, `editMessageText`, `deleteMessage`, `answerCallbackQuery`
  - `setWebhook`, `deleteWebhook`, `getWebhookInfo`
  - `answerInlineQuery`
  - `setMyCommands`, `getMyCommands`
  - `setChatMenuButton`, `getChatMenuButton`
  - `editMessageReplyMarkup`
  - `getFile`, `sendDocument`, `sendMediaGroup`
  - `sendInvoice`, `answerShippingQuery`, `answerPreCheckoutQuery`
  - `answerWebAppQuery`, `savePreparedInlineMessage`
  - `getBusinessConnection`, `readBusinessMessage`, `deleteBusinessMessages`
- Runtimes:
  - polling with offset advancement and graceful stop
  - webhook update processing with optional secret token validation
- Dispatcher/runtime:
  - routing for `message`, `edited_message`, `channel_post`, `edited_channel_post`, `callback_query`, `inline_query`, `chosen_inline_result`
  - routing for `shipping_query`, `pre_checkout_query`, `business_connection`, `business_message`, `edited_business_message`, `deleted_business_messages`
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
  - annotation-driven handlers:
    - `@BotController`, `@OnMessage`, `@OnCallbackQuery`, `@OnInlineQuery`, `@OnChosenInlineResult`
    - `@OnShippingQuery`, `@OnPreCheckoutQuery`
    - `@OnBusinessConnection`, `@OnBusinessMessage`, `@OnEditedBusinessMessage`, `@OnDeletedBusinessMessages`
  - `WebAppInitDataValidator` bean

## Not Supported Yet

- Payments advanced features beyond basic invoice/shipping/pre-checkout flow:
  - refunds/subscriptions/withdrawals/paid media.
- Business advanced surfaces (stories/profile/admin treasury and related flows).
- Full Web App platform runtime and frontend SDK wrapper.
- Full media hierarchy and media-group editing surface.
- Distributed state storages as built-in required implementations.
- Compile-time annotation processing/code generation.
- Production deployment automation/hardening for webhook infra.

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
