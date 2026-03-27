# Stage 3 Plan (Technical Scope & Roadmap)

Date: 2026-03-27  
Project: `ru.tardyon.botframework` (`ru.tardyon.botframework.telegram`)

## Sources Of Truth

- Telegram Bot API: https://core.telegram.org/bots/api
- Inline mode overview: https://core.telegram.org/bots/inline
- Telegram Bot API changelog (when needed for field/method recency): https://core.telegram.org/bots/api-changelog

Primary Bot API sections referenced for Stage 3:
- Getting updates / Update
- InlineQuery / ChosenInlineResult / answerInlineQuery
- sendMediaGroup / InputMedia / InputMediaPhoto / InputMediaVideo / InputMediaAudio / InputMediaDocument
- Sending files
- InlineKeyboardMarkup / InlineKeyboardButton
- setChatMenuButton / getChatMenuButton / MenuButton

---

## Scope By Module

## 1) `telegram-bot-framework-core`

### In Scope

- FSM / conversations runtime API
  - State definitions and transitions.
  - In-memory state storage implementation (mandatory for Stage 3 baseline).
  - Conversation context integration with existing `UpdateContext` / dispatcher pipeline.
- Inline mode support
  - Update handling for `inline_query` and `chosen_inline_result`.
  - API client method for `answerInlineQuery`.
  - Minimal DTO set needed for Stage 3 inline flow.
- Media groups support
  - API client method for `sendMediaGroup`.
  - Minimal `InputMedia*` model needed to send media albums.
  - Multipart support reuse for `attach://...` upload scenarios required by selected Stage 3 subset.
- Advanced inline button options (minimum subset backed by docs)
  - Extend inline button model beyond `url` / `callback_data` with selected official fields.
- Menu button API
  - Methods: `setChatMenuButton`, `getChatMenuButton`.
  - DTO model for `MenuButton*`.

### Not In Scope

- Distributed/remote FSM storage as required baseline.
- Full inline result hierarchy (all result types) in one iteration.
- Full media hierarchy surface and every optional parameter for all media methods.
- Payments/business APIs/full Web App platform.
- Code generation, fake Telegram server/testkit.

### Bot API Truth Anchors

- `answerInlineQuery`, `InlineQuery`, `ChosenInlineResult`
- `sendMediaGroup`, `InputMedia*`, `Sending files`
- `InlineKeyboardButton` field semantics (“exactly one action field” rule)
- `setChatMenuButton`, `getChatMenuButton`, `MenuButton*`

---

## 2) `telegram-bot-framework-spring-boot-starter`

### In Scope

- Annotation-driven API (thin integration over core runtime)
  - Annotation model for registering handlers/controllers in Spring context.
  - Registrar/scanner that binds annotated handlers to core `Router`/dispatcher APIs.
- FSM wiring
  - Auto-config of default in-memory state storage beans.
  - Injection/wiring from Spring beans into core FSM runtime.
- Keep mode switching (polling/webhook) behavior compatible with Stage 2.

### Not In Scope

- Moving runtime logic from core to starter.
- Spring-specific FSM engine that bypasses core FSM API.
- Heavy DSL/codegen-based annotations.

### Truth Anchors

- Spring Boot autoconfiguration conventions.
- Telegram semantics remain in core DTO/API and are not redefined by starter.

---

## 3) `telegram-bot-framework-demo`

### In Scope

- Conversation example (multi-step flow via core FSM API through starter wiring).
- Inline mode example (minimal query -> answer flow).
- Media group example (`sendMediaGroup` usage).
- Menu button + advanced inline button usage examples.

### Not In Scope

- Production deployment cookbook.
- Library/runtime implementation inside demo.
- Infra integrations (DB/Redis/Docker/etc.) as demo dependencies.

### Truth Anchors

- Demo only showcases behavior already implemented in core/starter.

---

## Functional Boundaries (Must Stay Unchanged)

- `core` is vanilla Java; no Spring dependencies.
- `starter` is thin adapter over core; no duplicated runtime logic.
- `demo` is sample app only.
- Group/package prefixes remain:
  - `groupId = ru.tardyon.botframework`
  - `ru.tardyon.botframework.telegram.*`

---

## Implementation Roadmap (Small Steps)

## Step 0: Contracts & Test Matrix

- Freeze Stage 3 acceptance checklist in tests-first style:
  - core unit tests for new DTO mapping/API serialization/runtime behavior.
  - starter tests for annotation scanning and FSM bean wiring.
  - demo compile/startup checks and minimal scenario coverage.

## Step 1: Core FSM Foundation

- Add core FSM abstractions:
  - state key/state value model,
  - transition API,
  - state storage interface + in-memory implementation.
- Integrate FSM context into dispatcher/update handling with deterministic sequential behavior.
- Add tests:
  - state set/get/clear,
  - transition flow,
  - middleware + FSM interaction.

## Step 2: Core Inline Mode (Minimal)

- Extend update model/dispatcher routing for:
  - `inline_query`,
  - `chosen_inline_result`.
- Add `answerInlineQuery` client method + request/response mapping.
- Add tests:
  - JSON mapping for inline updates,
  - request serialization for `answerInlineQuery`,
  - dispatch routing behavior.

## Step 3: Core Media Groups

- Add `sendMediaGroup` method and minimal request model aligned with docs.
- Add `InputMedia*` subset models required by Stage 3 demo.
- Reuse multipart transport for attached uploads where applicable.
- Add tests:
  - JSON/multipart request construction,
  - response mapping (`Array<Message>`).

## Step 4: Advanced Inline Buttons

- Extend `InlineKeyboardButton` model for selected official fields.
- Preserve “exactly one action field” validation rule per Bot API.
- Add builder updates and serialization tests.

## Step 5: Menu Button API

- Add core methods:
  - `setChatMenuButton`,
  - `getChatMenuButton`.
- Add `MenuButton` model variants and tests for mapping/serialization.

## Step 6: Starter Annotation Layer

- Introduce minimal annotation set and scanner/registrar.
- Map annotated methods to core router handlers.
- Wire FSM storage/runtime beans with override points.
- Add starter tests:
  - annotation discovery,
  - handler registration,
  - FSM wiring correctness.

## Step 7: Demo Stage 3 Examples

- Add multi-step conversation handler.
- Add inline mode example handlers.
- Add media group and menu button examples.
- Update demo README and application config sections.

## Step 8: Final Verification & Audit

- Run full multi-module test suite.
- Audit Stage 3 implementation vs official docs and architecture boundaries.
- Update root docs with Stage 3 supported/not-supported matrix.

---

## Deliberately Deferred Features

- Payments APIs.
- Business APIs.
- Full Web App platform.
- Code generation.
- Fake Telegram server / testkit.
- Distributed state storages as required Stage 3 baseline.
- Full exhaustive support of every inline result/media/button variant in one stage.

