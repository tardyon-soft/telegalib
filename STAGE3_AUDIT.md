# Stage 3 Audit Report

Date: 2026-03-27  
Source of truth:
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Inline Bots](https://core.telegram.org/bots/inline)
- optional [Bot API changelog](https://core.telegram.org/bots/api-changelog)

## Confirmed By Docs

- New update types and methods are implemented and mapped:
  - `inline_query` / `chosen_inline_result` in `Update`
  - `answerInlineQuery`
  - `setChatMenuButton`, `getChatMenuButton`
  - `sendMediaGroup`
- Inline objects are modeled with documented fields used in Stage 3 scope:
  - `InlineQuery`, `ChosenInlineResult`
  - `InlineQueryResultArticle`, `InlineQueryResultPhoto`
  - `InputTextMessageContent`
- Menu button model matches documented type union:
  - `MenuButtonDefault`, `MenuButtonCommands`, `MenuButtonWebApp`
- Media group model matches documented group types:
  - `InputMediaPhoto`, `InputMediaVideo`, `InputMediaDocument`, `InputMediaAudio`
- Semantic constraints implemented from docs:
  - `answerInlineQuery` enforces max 50 results
  - `answerInlineQuery` validates `next_offset` byte length (`<= 64`)
  - `sendMediaGroup` validates size `2..10`
  - `sendMediaGroup` validates document/audio homogeneous album rule
- Chosen inline feedback behavior:
  - runtime does not assume chosen-inline updates are always available;
  - docs note that inline feedback must be enabled via BotFather.

## Simplified Intentionally (Stage 3)

- Inline results:
  - only minimal result hierarchy in current scope (`Article`, `Photo`, `CachedPhoto` already present in core).
- `sendMediaGroup` request surface intentionally minimal:
  - `chat_id`, `media`
  - no full optional parameter matrix from latest Bot API.
- Menu button support:
  - DTO/method layer provided;
  - no Web App runtime platform behavior beyond DTO mapping.
- Annotation-driven starter API is a thin adapter over core router/filters:
  - no expression language
  - no compile-time processing.

## Deferred To Future Stages

- Full inline result type coverage and advanced inline interaction APIs.
- Media-group edit APIs and broader media option matrix.
- Payments and business APIs.
- Full Web App platform support.
- Distributed FSM storage implementations as built-in mandatory modules.
- Production deployment hardening (operational concerns outside current scope).

## Module Boundaries Confirmed

- `telegram-bot-framework-core`
  - no Spring dependencies in code/dependencies.
  - contains runtime/library logic (API client, dispatcher, FSM, inline/media/menu features).
- `telegram-bot-framework-spring-boot-starter`
  - depends on core.
  - provides only wiring/autoconfiguration/lifecycle/webhook endpoint/annotation integration.
  - does not duplicate core runtime logic.
- `telegram-bot-framework-demo`
  - depends on starter.
  - demonstrates usage scenarios only.
  - does not contain library runtime implementation.

## Stage 1/2 Compatibility Check

- Stage 1/2 behavior remains covered and passing in full suite:
  - polling/webhook/runtime/filter/wrapper/tests still pass.
  - multi-module verification: `./gradlew test` passed on 2026-03-27.

## Open Questions / Gaps

- Core intentionally validates only documented constraints explicitly included in scope; not all optional Bot API limits are exhaustively enforced.
- Demo uses externally supplied assets/env vars for some scenarios (`albumtest`, webhook URL), which is expected for sample app behavior.
