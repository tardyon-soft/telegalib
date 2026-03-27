# Stage 4 Plan (Technical Scope & Roadmap)

Date: 2026-03-27  
Project: `ru.tardyon.botframework` (`ru.tardyon.botframework.telegram`)

## Sources Of Truth

- Telegram Bot API: https://core.telegram.org/bots/api
- Telegram Mini Apps: https://core.telegram.org/bots/webapps
- Bot Payments API (physical goods flow reference): https://core.telegram.org/bots/payments
- Bot Payments API for Digital Goods and Services (Telegram Stars): https://core.telegram.org/bots/payments-stars
- Telegram Bot API changelog (when method/field recency matters): https://core.telegram.org/bots/api-changelog

Primary Bot API sections referenced for Stage 4:
- Getting updates / Update (`shipping_query`, `pre_checkout_query`, `business_connection`, `business_message`, `edited_business_message`, `deleted_business_messages`)
- Payments section in Bot API manual:
  - `sendInvoice`
  - `createInvoiceLink`
  - `answerShippingQuery`
  - `answerPreCheckoutQuery`
  - payment-related objects (`LabeledPrice`, `ShippingAddress`, `OrderInfo`, `ShippingOption`, `SuccessfulPayment`, `RefundedPayment` when needed)
- Telegram Stars guidance for digital goods (`currency = XTR`, invoice/pre-checkout/success flow)
- Mini Apps / Web Apps in Bot API:
  - `WebAppInfo`, `WebAppData`, `answerWebAppQuery`
  - menu button/web_app launch integration as applicable
- Mini Apps validation guide (bots/webapps):
  - `initData` signature validation (HMAC-SHA-256 with `WebAppData`)
  - `auth_date` freshness checks
  - optional third-party signature validation notes (deferred unless required)
- Business account support in Bot API:
  - `BusinessConnection`
  - `getBusinessConnection`
  - `readBusinessMessage`
  - `deleteBusinessMessages`
  - message sending/editing methods with `business_connection_id` where documented

---

## Scope By Module

## 1) `telegram-bot-framework-core`

### In Scope

- Payments / invoice flow (minimal but complete Stage 4 baseline)
  - Bot API methods:
    - `sendInvoice`
    - `createInvoiceLink`
    - `answerShippingQuery`
    - `answerPreCheckoutQuery`
  - Update/DTO coverage for:
    - `shipping_query`
    - `pre_checkout_query`
    - `successful_payment` in `Message`
  - Thin convenience helpers for invoice/pre-checkout handling if they map directly to existing runtime wrappers.
- Mini Apps / Web Apps bot-side support
  - DTO support for `web_app` button/menu fields required by Stage 4 scope.
  - `answerWebAppQuery` method.
  - `initData` validation helper utility in core (framework-agnostic), including:
    - canonical data-check-string construction,
    - HMAC verification,
    - configurable max age check based on `auth_date`.
- Business APIs basic support
  - Update model support for business updates listed above.
  - Methods:
    - `getBusinessConnection`
    - `readBusinessMessage`
    - `deleteBusinessMessages`
  - Business-aware request fields where already documented on existing methods (no alternate runtime branch).

### Not In Scope

- Paid media APIs as a Stage 4 deliverable.
- Gifts/Stars treasury operations, ad spending, withdrawals.
- Full Web App frontend SDK wrappers or client-side JS abstractions.
- Full business API surface (all business methods/objects in one stage).
- Distributed stores/caching layers dedicated to payments/business.

### Bot API Truth Anchors

- Bot API Payments section (`sendInvoice`, `answerShippingQuery`, `answerPreCheckoutQuery`, `SuccessfulPayment`)
- `createInvoiceLink`
- Bot API objects for shipping/pre-checkout/payment receipt data
- Bot API `WebAppInfo` / `WebAppData` / `answerWebAppQuery`
- bots/webapps “Validating data received via the Mini App”
- Bot API business methods/updates (`BusinessConnection`, `getBusinessConnection`, `readBusinessMessage`, `deleteBusinessMessages`)

---

## 2) `telegram-bot-framework-spring-boot-starter`

### In Scope

- Payment handlers integration
  - Expose easy wiring for handling `shipping_query`, `pre_checkout_query`, and payment receipt updates through existing router/annotation registration.
- WebApp/business wiring
  - Register core `initData` validation helper as bean(s) when enabled.
  - Wire business-capable core services/handlers without moving logic out of core.
- Properties extensions (minimal)
  - Add only necessary properties for:
    - payment-related toggles (if needed for lifecycle defaults),
    - web app init-data validation settings (e.g., max age/clock skew),
    - business mode feature flags only when they control starter wiring.

### Not In Scope

- Re-implementing payment/business runtime in starter.
- Spring-only payment FSM separate from core runtime.
- Heavy annotation DSL beyond existing Stage 3 model.

### Truth Anchors

- Spring Boot autoconfiguration conventions.
- Telegram semantics remain defined by core DTO/API layer.

---

## 3) `telegram-bot-framework-demo`

### In Scope

- Invoice demo scenario
  - command/handler that sends invoice,
  - demonstrates pre-checkout handling,
  - demonstrates successful payment handling.
- Web App demo scenario
  - button/menu launch example,
  - `web_app_data` / `answerWebAppQuery` flow sample,
  - server-side `initData` validation helper usage example.
- Business demo scenario (minimal)
  - business connection fetch/read/delete examples where safe and demonstrative,
  - clearly marked as sample wiring.

### Not In Scope

- Production commerce workflows (inventory, reconciliation, accounting).
- Full Mini App frontend implementation and SDK wrappers.
- Real payout/withdrawal flows or treasury management.
- Extra infrastructure (DB/Redis/Docker) as mandatory demo dependencies.

### Truth Anchors

- Demo shows only behavior already implemented in core/starter.

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

## Step 0: Contract Freeze & Verification Matrix

- Freeze Stage 4 acceptance checklist and test matrix before coding:
  - core DTO/API serialization tests,
  - starter wiring/property tests,
  - demo compile + scenario smoke checks.

## Step 1: Core Payments Baseline

- Add payment DTO/request/response subset for invoice + pre-checkout + success receipt.
- Implement methods: `sendInvoice`, `createInvoiceLink`, `answerShippingQuery`, `answerPreCheckoutQuery`.
- Extend update/message models for `shipping_query`, `pre_checkout_query`, `successful_payment`.
- Add unit tests for JSON mapping and error envelope handling.

## Step 2: Core Web Apps Bot-side Layer

- Add DTO fields needed for web app launch and incoming web app data.
- Implement `answerWebAppQuery`.
- Implement `initData` validation helper (HMAC and timestamp checks).
- Add deterministic tests with official algorithm examples.

## Step 3: Core Business Basic Layer

- Add/update DTO support for business updates.
- Implement methods: `getBusinessConnection`, `readBusinessMessage`, `deleteBusinessMessages`.
- Extend wrappers only where direct Bot API semantics remain transparent.
- Add tests for parsing and request serialization.

## Step 4: Starter Stage 4 Wiring

- Extend starter properties minimally.
- Add beans/adapters for payment handlers and webapp init-data helper.
- Wire business-facing core services/routers.
- Add lifecycle and bean-creation tests (without duplicating core logic).

## Step 5: Demo Stage 4 Scenarios

- Add invoice demo flow (`/buy`-style command + pre-checkout + success handling).
- Add webapp demo flow (button/menu + callback/update handling).
- Add business demo stubs/examples with explicit constraints.
- Update demo README and configuration examples.

## Step 6: Final Stage 4 Verification & Audit

- Run full multi-module tests.
- Audit against official docs and module boundaries.
- Publish Stage 4 audit report with “confirmed/simplified/deferred” sections.

---

## Deliberately Deferred Features

- Paid media APIs.
- Gifts/Stars treasury and advanced Stars account operations.
- Withdrawals and payout-specific flows.
- Full Web App frontend SDK wrapper.
- Code generation.
- Fake Telegram server/testkit.
- Full business API surface beyond Stage 4 baseline methods and updates.
