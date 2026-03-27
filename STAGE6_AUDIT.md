# Stage 6 Final Audit Report

Date: 2026-03-27

Scope audited:
- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`
- `telegram-bot-framework-demo`
- `telegram-bot-framework-botapi-generator`
- `telegram-bot-framework-testkit`

## Confirmed By Docs

Source of truth used:
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Bot API changelog](https://core.telegram.org/bots/api-changelog)

Confirmed facts used by Stage 6 implementation:
- Local Bot API server support exists and allows:
  - using own server instead of `https://api.telegram.org`,
  - upload via local path and `file://` URI scheme,
  - webhook over HTTP/local IP/any port,
  - absolute local `file_path` values.  
  Reference: `Using a Local Bot API Server` section in Bot API docs.
- Changelog alignment for capability matrix:
  - `PAID_MEDIA` mapped to Bot API 7.6 (`sendPaidMedia`, paid media classes).
  - `STARS` mapped to Bot API 7.4 (Stars methods).
  - `PRIVATE_CHAT_TOPICS` mapped to Bot API 9.3 (private topics + `message_thread_id` in private chats).
  - `SEND_MESSAGE_DRAFT` mapped to generally available in Bot API 9.5 (allowed for all bots).

## Simplified Intentionally

- Capability layer is explicit/manual only:
  - no auto-detection by probing API/network errors,
  - no runtime permission engine.
- Starter provides integration beans (`BotApiTransportProfile`, `DiagnosticsHooks`, `BotApiCapabilities`) without duplicating core runtime.
- Generator scope is MVP subset generation only; runtime layers remain manual.
- Testkit is deterministic and lightweight, not a full Telegram protocol emulator.

## Deferred To Future Stages

- Full Bot API generation coverage (complex unions/edge objects).
- Protocol-accurate fake Telegram server behavior in testkit.
- Automated changelog-to-capability synchronization pipeline.
- Advanced diagnostics exporters (OpenTelemetry/Micrometer bridges) as first-class modules.

## Module Boundaries Confirmed

- `core`:
  - no Spring imports/dependencies in main sources,
  - contains production runtime logic.
- `starter`:
  - depends on core,
  - thin adapter/wiring only.
- `demo`:
  - example app only,
  - `testkit` used only in `testImplementation`.
- `botapi-generator`:
  - tooling-only module, no runtime dependency from app modules.
- `testkit`:
  - testing support module, not used as production dependency.

## Open Questions / Gaps

- `BUSINESS_STORIES_CHECKLISTS` capability is intentionally conservative (`9.1`) while stories appeared earlier; current mapping is safe but coarse.
- Starter default local base URL (`http://127.0.0.1:8081`) is a convenience default, not a documented Telegram-mandated value; it is overrideable via properties.

## Stage 1–5 Compatibility Status

- Full multi-module verification run passed (`clean test`).
- No regressions detected in Stage 1–5 behavior from Stage 6 additions.
