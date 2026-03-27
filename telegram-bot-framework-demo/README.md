# telegram-bot-framework-demo

Spring Boot demo for `telegram-bot-framework-spring-boot-starter` with Stage 5 scenarios using annotation-driven API.

## What demo shows

- Stage 5 monetization commands:
  - `/paid-media-test` -> `sendPaidMedia`
  - `/stars-balance` -> `getMyStarBalance` + `getStarTransactions`
  - `/gift-test` -> `sendGift`
  - `/premium-gift-test` -> `giftPremiumSubscription`
  - `/channel-subscription-init` -> `createChatSubscriptionInviteLink`
- Stage 5 business commands:
  - `/business-story-test` -> `postStory`
  - `/business-checklist-test` -> `sendChecklist`
  - `/business-gifts-test` -> `getBusinessAccountGifts`
- Stage 5 annotation handlers for service messages:
  - `@OnMessage(giftPresent = true)`
  - `@OnBusinessMessage(refundedPaymentPresent = true)`
- Stage 3/4 compatibility scenarios are kept:
  - FSM `/startform`
  - callback `menu:*`
  - inline mode examples
  - invoice `/buy-test`
  - web app `/webapp`
  - media group `/albumtest`

## Stage 5 prerequisites

- Telegram Stars and monetization:
  - Bot must have enough Stars balance for paid media/gifts/premium gifting.
  - `DEMO_PAID_MEDIA_FILE_ID` must be a valid Telegram `file_id` for photo/video.
  - `DEMO_GIFT_ID` must be an existing gift from `getAvailableGifts`.
- Channel subscription links:
  - Bot must be admin in target channel and have rights required by Telegram for invite links.
  - Set `DEMO_CHANNEL_CHAT_ID` (`@channel_username` or numeric chat id).
- Business operations:
  - Business connection must exist (`DEMO_BUSINESS_CONNECTION_ID`).
  - Bot must have business rights needed for stories/checklists/gifts/stars operations.
  - For story demo set `DEMO_BUSINESS_STORY_FILE_ID`.

## Environment variables

Required:

- `BOT_TOKEN`

Optional for polling/webhook:

- `BOT_WEBHOOK_PUBLIC_URL`
- `BOT_WEBHOOK_SECRET_TOKEN`

Optional for Stage 4 compatibility:

- `PAYMENT_PROVIDER_TOKEN`
- `DEMO_STARS_MODE`
- `DEMO_WEB_APP_URL`

Optional for Stage 5 demos:

- `DEMO_PAID_MEDIA_FILE_ID`
- `DEMO_PAID_MEDIA_TYPE` (`photo` or `video`)
- `DEMO_PAID_MEDIA_STAR_COUNT` (default `1`)
- `DEMO_GIFT_ID`
- `DEMO_PREMIUM_MONTH_COUNT` (`3`, `6`, `12`)
- `DEMO_CHANNEL_CHAT_ID`
- `DEMO_CHANNEL_SUB_NAME`
- `DEMO_CHANNEL_SUB_PRICE`
- `DEMO_BUSINESS_CONNECTION_ID`
- `DEMO_BUSINESS_STORY_FILE_ID`

Optional for `/albumtest`:

- local files:
  - `DEMO_ALBUM_FILE1`
  - `DEMO_ALBUM_FILE2`
- or Telegram file ids:
  - `DEMO_ALBUM_FILE_ID1`
  - `DEMO_ALBUM_FILE_ID2`

## Run demo (polling)

```bash
export BOT_TOKEN=123456:ABCDEF
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling'
```

## Run demo (webhook)

```bash
export BOT_TOKEN=123456:ABCDEF
export BOT_WEBHOOK_PUBLIC_URL=https://example.com
export BOT_WEBHOOK_SECRET_TOKEN=super-secret
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=webhook'
```

Default webhook endpoint path: `/telegram/webhook`.

## Notes

- Demo stays as usage example only; runtime logic remains in `core` and `starter`.
- No DB/Redis/Docker/MTProto/production billing logic is included.
