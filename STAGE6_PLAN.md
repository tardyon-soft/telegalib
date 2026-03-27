# Stage 6 Technical Scope and Roadmap

Date: 2026-03-27

## Sources of Truth

Only official Telegram Bot API docs are used:
- https://core.telegram.org/bots/api
- https://core.telegram.org/bots/api-changelog

Relevant API sections:
- `Making requests`
- `Making requests when getting updates`
- `Using a Local Bot API Server`
- `Getting updates`
- `Update`
- `Recent changes` and `api-changelog`

## Stage 6 Goal

Stage 6 is platform/tooling maturity over Stage 5, not a new business-feature wave.

Target modules:
- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`
- `telegram-bot-framework-demo`
- `telegram-bot-framework-botapi-generator` (new)
- `telegram-bot-framework-testkit` (new)

## Module Scope

### CORE (`telegram-bot-framework-core`)

In scope:
- Transport profiles for Telegram cloud API and local Bot API server.
- Capability/version model for feature gating based on Bot API version/capabilities.
- Diagnostics hooks (request/response lifecycle hooks, timing/error hooks) without forcing a logging stack.

Out of scope:
- Spring integration in core.
- New Telegram business feature surfaces beyond Stage 5.
- MTProto runtime or non-Bot API transports.

Docs source:
- `Making requests`
- `Using a Local Bot API Server`
- `Getting updates`
- `Update`
- `api-changelog`

### GENERATOR (`telegram-bot-framework-botapi-generator`)

In scope:
- Tooling pipeline that generates DTO/method scaffolding from official Bot API source material.
- Deterministic generation output suitable for manual review and selective adoption in core.
- Verification mode (diff/check) for API drift detection.

Out of scope:
- Runtime dependency for applications.
- Automatic replacement of hand-written runtime logic.
- Unreviewed direct overwrites of existing production classes.

Docs source:
- `bots/api` structure and object/method definitions
- `api-changelog` for change tracking

### TESTKIT (`telegram-bot-framework-testkit`)

In scope:
- Fake Bot API server for deterministic tests.
- Polling/webhook update simulators.
- Request assertions (method path, payload, headers, multipart structure).
- Fixtures for common Bot API envelopes and updates.

Out of scope:
- Production runtime use.
- Distributed cloud testing infrastructure.
- Full Telegram protocol emulation.

Docs source:
- `Making requests`
- `Making requests when getting updates`
- `Getting updates`
- `Update`

### STARTER (`telegram-bot-framework-spring-boot-starter`)

In scope:
- Test-friendly wiring (easy testkit/fake endpoint integration).
- Diagnostics integration with core hooks (bean-level adapter only).
- Keep manual Router wiring and non-annotation flows functional.

Out of scope:
- Duplication of core runtime logic.
- Starter-only behavior that diverges from core semantics.

Docs source:
- Bot API transport/update semantics from `bots/api` (for integration correctness)

### DEMO (`telegram-bot-framework-demo`)

In scope:
- Profile/examples for:
  - cloud mode (`https://api.telegram.org`)
  - local Bot API mode
  - fake test mode via testkit
- Minimal scenarios to demonstrate configuration and diagnostics visibility.

Out of scope:
- Library/runtime implementation.
- Production deployment infrastructure.

Docs source:
- `Making requests`
- `Using a Local Bot API Server`
- `Getting updates`

## Implementation Roadmap (Small Steps)

1. Project scaffolding for Stage 6 modules
- Add module directories:
  - `telegram-bot-framework-botapi-generator`
  - `telegram-bot-framework-testkit`
- Update `settings.gradle.kts` includes.
- Add minimal Gradle build files and package roots.

2. Core transport profile foundation
- Add explicit transport profile config (cloud/local).
- Keep existing `HttpClient` flow; centralize base URL/profile resolution.
- Add compatibility tests for existing Stage 1-5 client methods.

3. Core capability/version model
- Add Bot API capability representation and version metadata model.
- Add conservative feature-gating helpers that do not invent Telegram semantics.
- Add docs/tests for fallback behavior when capability unknown.

4. Core diagnostics hooks
- Add hook interfaces around API call lifecycle and update processing.
- Ensure no-op default behavior and no mandatory logging dependency.
- Verify polling/webhook flows still deterministic.

5. Generator MVP
- Implement parse-and-generate pipeline for selected method/DTO scaffolding.
- Add generated output layout and formatting rules.
- Add dry-run/check mode for CI drift detection.

6. Testkit fake server MVP
- Implement fake HTTP server endpoints for core Stage 1-5 method tests.
- Add canned response queue + request recorder/assertions.
- Add webhook and polling simulators.

7. Starter Stage 6 integration
- Wire diagnostics hooks and transport profile beans into starter.
- Provide test-friendly endpoint configuration for fake server/local mode.
- Keep starter thin and core-first.

8. Demo Stage 6 profiles
- Add cloud/local/fake profiles.
- Add concise examples showing profile switch and diagnostics hooks.

9. Verification and audit pass
- Full multi-module tests.
- Stage 6 audit document for module boundaries and doc alignment.

## Design Constraints

- Group/package baseline:
  - groupId: `ru.tardyon.botframework`
  - base package: `ru.tardyon.botframework.telegram`
- `core` remains Spring-free and production runtime source of truth.
- `starter` remains thin adapter.
- `generator` and `testkit` are non-production-runtime modules.
- No speculative abstractions beyond validated Stage 6 goals.

## Deliberately Deferred Features

- MTProto runtime features.
- New Telegram business feature waves beyond Stage 5.
- Frontend SDK/WebApp client wrappers.
- Distributed cloud test infrastructure.
- Code generation that bypasses manual review/acceptance flow.
