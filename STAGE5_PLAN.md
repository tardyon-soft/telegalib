# Stage 5 Plan (Technical Scope & Roadmap)

Date: 2026-03-27  
Project: `ru.tardyon.botframework` (`ru.tardyon.botframework.telegram`)

## Sources Of Truth

- Telegram Bot API: https://core.telegram.org/bots/api
- Bot Payments (physical goods baseline): https://core.telegram.org/bots/payments
- Bot Payments for digital goods / Telegram Stars: https://core.telegram.org/bots/payments-stars
- Bot API changelog (when recency matters): https://core.telegram.org/bots/api-changelog

Primary Bot API sections to anchor Stage 5:

- Methods:
  - `sendPaidMedia`
  - `getMyStarBalance`
  - `getStarTransactions`
  - `refundStarPayment`
  - `editUserStarSubscription`
  - `getAvailableGifts`
  - `sendGift`
  - `giftPremiumSubscription`
  - `createChatSubscriptionInviteLink`
  - `editChatSubscriptionInviteLink`
  - `revokeChatInviteLink` (for subscription invite link revocation flow)
  - business advanced methods:
    - `postStory`, `editStory`, `deleteStory`
    - `sendChecklist`, `editMessageChecklist`
    - `setBusinessAccountGiftSettings`
    - `getBusinessAccountStarBalance`
    - `transferBusinessAccountStars`
    - `getBusinessAccountGifts`
    - `convertGiftToStars`
    - `transferGift`
    - `upgradeGift`
- Objects / message/update model:
  - paid media objects (`PaidMediaInfo`, `PaidMedia*`, `InputPaidMedia*`, `PaidMediaPurchased`)
  - stars/revenue objects (`StarAmount`, `StarTransactions`, `StarTransaction`, `RevenueWithdrawalState*`)
  - gifts objects (`Gift`, `GiftInfo`, `UniqueGiftInfo`, `OwnedGifts`, related gift types from docs)
  - channel subscription invite link parameters (`subscription_period`, `subscription_price`)
  - business account and checklist/story related objects from methods above
  - service message fields in `Message` (gift/payment/checklist/story-related fields that are explicitly documented)

If a field/method naming or constraints differ in docs at implementation time, docs must override this plan.

---

## Module Scope

## 1) `telegram-bot-framework-core`

### In Scope

- Paid media:
  - low-level method: `sendPaidMedia`
  - DTO support for minimum useful paid-media object graph and service-message coverage tied to paid media purchase info.
- Stars treasury/revenue operations:
  - `getMyStarBalance`
  - `getStarTransactions`
  - `refundStarPayment`
  - `editUserStarSubscription`
  - DTO support for returned star/revenue transaction objects in documented Stage 5 subset.
- Gifts / premium gifting:
  - `getAvailableGifts`
  - `sendGift`
  - `giftPremiumSubscription`
  - message/service DTO fields for gift-related events in Stage 5 subset.
- Channel subscription invite links:
  - `createChatSubscriptionInviteLink`
  - `editChatSubscriptionInviteLink`
  - revocation via `revokeChatInviteLink` in subscription flow.
- Advanced business operations (documented methods only):
  - stories (`postStory`, `editStory`, `deleteStory`)
  - checklists (`sendChecklist`, `editMessageChecklist`)
  - business gift/star operations (`setBusinessAccountGiftSettings`, `getBusinessAccountStarBalance`,
    `transferBusinessAccountStars`, `getBusinessAccountGifts`, `convertGiftToStars`, `transferGift`, `upgradeGift`)
- Dispatcher/runtime support in core for new Stage 5 update/service-message types where docs define them as routable incoming data.

### Not In Scope

- MTProto-only features and non-Bot-API surfaces.
- Full payout/backoffice system or accounting engine around Stars transactions.
- Full gifts marketplace logic beyond direct Bot API methods.
- Full frontend/web app SDK wrappers.
- Any local fake Telegram server/testkit framework.

### Truth Anchors

- Bot API methods and objects listed in “Sources Of Truth”.
- Payments/Stars semantics from `bots/payments` and `bots/payments-stars`.

---

## 2) `telegram-bot-framework-spring-boot-starter`

### In Scope

- Thin wiring for Stage 5 core features:
  - bean creation for Stage 5 core helpers/clients already implemented in core.
- Annotation integration extension:
  - support Stage 5 update/service-message handler routing through existing router+registrar model.
- Method argument resolution extensions for newly exposed Stage 5 core DTO/wrappers.
- Keep manual `Router` wiring fully supported (no annotation-only lock-in).

### Not In Scope

- Re-implementing paid media/stars/gifts/business runtime logic in starter.
- Spring-only semantic forks that diverge from core method behavior.
- Heavy DSL/EL for annotations.

### Truth Anchors

- Spring Boot autoconfiguration conventions.
- Core runtime remains the source of behavior.

---

## 3) `telegram-bot-framework-demo`

### In Scope

- Paid media scenario example.
- Stars/gifts scenario example.
- Channel subscription invite link scenario example.
- Business advanced operations example:
  - stories/checklists/gifts/stars operations as minimal demonstrative flows.
- Profile-based or property-based runnable examples (polling/webhook compatible with existing starter behavior).

### Not In Scope

- Production billing/reconciliation pipeline.
- Secrets management platform, DB, Redis, Docker as mandatory requirements.
- Any library runtime implementation duplicated inside demo.

### Truth Anchors

- Demo only demonstrates what core/starter already provide.

---

## Architecture Boundaries (Must Stay Fixed)

- groupId: `ru.tardyon.botframework`
- base package: `ru.tardyon.botframework.telegram`
- `core`:
  - vanilla Java only
  - no Spring dependencies
  - all runtime/business logic lives here
- `starter`:
  - thin adapter over core
  - no duplicate runtime semantics
- `demo`:
  - usage sample only
  - no library internals

---

## Implementation Roadmap (Small Steps)

1. **Step 0 — Stage 5 Contract Freeze**
   - Build matrix of methods/DTOs/update/service-message fields to be supported in Stage 5.
   - Add GAP notes for any doc ambiguity before coding.

2. **Step 1 — Core Paid Media Layer**
   - Add `sendPaidMedia` request/response support.
   - Add minimum `InputPaidMedia*` and paid-media model mapping.
   - Add tests for JSON and multipart behaviors.

3. **Step 2 — Core Stars Treasury/Revenue**
   - Add `getMyStarBalance`, `getStarTransactions`, `refundStarPayment`, `editUserStarSubscription`.
   - Add DTO mapping tests and request validation tests.

4. **Step 3 — Core Gifts Layer**
   - Add `getAvailableGifts`, `sendGift`, `giftPremiumSubscription`.
   - Add gift-related message/service DTO mapping.

5. **Step 4 — Core Channel Subscription Links**
   - Add subscription invite link method support (`create/edit` + revoke flow via existing `revokeChatInviteLink` method support).
   - Add validation for documented constraints.

6. **Step 5 — Core Advanced Business Ops**
   - Add stories/checklists/business gifts/stars methods from Stage 5 scope.
   - Add routing/model coverage for related incoming service/update data when documented.

7. **Step 6 — Starter Stage 5 Integration**
   - Extend annotation registration and argument resolution for new Stage 5 handler types.
   - Add/adjust bean wiring for Stage 5 helpers.
   - Verify manual router wiring remains intact.

8. **Step 7 — Demo Stage 5 Scenarios**
   - Add examples for paid media, gifts/stars, channel subscriptions, business advanced ops.
   - Update demo README and application config examples.

9. **Step 8 — Verification & Audit**
   - Full multi-module test pass.
   - Stage 5 final audit report:
     - confirmed by docs
     - simplified intentionally
     - deferred
     - boundaries confirmed

---

## Deliberately Deferred Features

- MTProto-only capabilities outside Bot API.
- Code generation for DTO/methods.
- Fake Telegram server/testkit.
- Full frontend/webapp SDK wrapper stack.
- Paid media purchase behavior outside documented Bot API coverage.
- Non-documented abstractions or inferred semantics not present in official docs.
