# Stage 5 Audit Report

Date: 2026-03-27

## Confirmed by docs

Source of truth:
- https://core.telegram.org/bots/api
- https://core.telegram.org/bots/payments
- https://core.telegram.org/bots/payments-stars
- https://core.telegram.org/bots/api-changelog

Verified Stage 5 methods exist in core client interface/implementation and are mapped to Bot API method names:
- `sendPaidMedia`
- `getMyStarBalance`
- `getStarTransactions`
- `refundStarPayment`
- `editUserStarSubscription`
- `getAvailableGifts`
- `sendGift`
- `giftPremiumSubscription`
- `getUserGifts`
- `getChatGifts`
- `createChatSubscriptionInviteLink`
- `editChatSubscriptionInviteLink`
- `postStory`
- `editStory`
- `deleteStory`
- `repostStory`
- `sendChecklist`
- `editMessageChecklist`
- `setBusinessAccountGiftSettings`
- `getBusinessAccountStarBalance`
- `transferBusinessAccountStars`
- `getBusinessAccountGifts`
- `convertGiftToStars`
- `upgradeGift`
- `transferGift`

Verified Stage 5 DTO groups are present and mapped in core:
- paid media: `PaidMediaInfo`, `PaidMedia*`, `InputPaidMedia*`
- stars: `StarAmount`, `StarTransaction`, `StarTransactions`
- gifts/subscriptions: `Gift`, `Gifts`, `OwnedGift*`, `OwnedGifts`, `GiftInfo`, `UniqueGift*`, `AcceptedGiftTypes`
- stories/checklists: `Story`, `InputStoryContent*`, `StoryArea*`, `Checklist`, `ChecklistTask`, `InputChecklist`, `InputChecklistTask`

Verified documented constraints represented in request validation:
- `sendPaidMedia`: star count and item count bounds
- `getStarTransactions`: `limit` 1..100
- `createChatSubscriptionInviteLink`: `subscription_period=2592000`, `subscription_price` bounds, `name` length
- `giftPremiumSubscription`: month set and star count mapping
- `postStory`/`repostStory`: allowed `active_period` values
- gift list methods: pagination bounds where documented

## Fixes made during audit

Documented mismatch corrected:
- `getStarTransactions.offset` changed from `String` to `Integer` in core request model.
- `StarTransactions.nextOffset` changed from `String` to `Integer` in core response model.
- Core tests updated accordingly.

Rationale:
- Bot API defines offset for `getStarTransactions` as numeric skip count.

## Simplified intentionally

- No local business-rights policy engine; rights are documented and delegated to Telegram API enforcement.
- No over-modeling of unrelated Bot API surfaces outside Stage 5 scope.
- Starter remains adapter-only and does not reimplement dispatcher/router/runtime behavior.

## Deferred to future stages

- Bot API surfaces outside Stage 5 scope (payments/business/media areas not requested in current stage plan).
- Infrastructure-heavy features (distributed storage, external billing workflows, deployment automation) not required by Stage 5.

## Module boundaries confirmed

- `telegram-bot-framework-core`
  - no Spring imports/dependencies in main runtime code
  - contains runtime/library logic for Stage 1-5 features
- `telegram-bot-framework-spring-boot-starter`
  - depends on core and provides wiring/autoconfiguration/lifecycle/annotation integration
  - does not duplicate core transport/dispatcher/runtime implementations
- `telegram-bot-framework-demo`
  - depends on starter
  - remains usage example; no library runtime duplication

## Open questions / gaps

- Bot API evolves frequently; future audit should re-check newly added optional fields for Stage 5 objects after each Bot API release.
- Current implementation follows documented constraints used in Stage 5 scope; broader optional field coverage may be expanded in next stage only if required by scope.
