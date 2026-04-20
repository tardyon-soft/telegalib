# telegram-bot-framework-demo

Spring Boot demo for `telegram-bot-framework-spring-boot-starter` with Stage 6 transport/diagnostics/test maturity on top of Stage 5 scenarios.

## What demo shows

- Stage 5 monetization commands:
  - `/paid_media_test` -> `sendPaidMedia`
  - `/stars_balance` -> `getMyStarBalance` + `getStarTransactions`
  - `/gift_test` -> `sendGift`
  - `/premium_gift_test` -> `giftPremiumSubscription`
  - `/channel_subscription_init` -> `createChatSubscriptionInviteLink`
- Channel membership/admin checks:
  - `/channel_member_check <chat_id> [user_id]` -> `getChatMember`
  - `/channel_admins <chat_id>` -> `getChatAdministrators` + `getChatMemberCount`
  - `/bot_channel_admin_check <chat_id>` -> `getMe` + `getChatMember`
- Stage 5 business commands:
  - `/business_story_test` -> `postStory`
  - `/business_checklist_test` -> `sendChecklist`
  - `/business_gifts_test` -> `getBusinessAccountGifts`
- Stage 5 annotation handlers for service messages:
  - `@OnMessage(giftPresent = true)`
  - `@OnBusinessMessage(refundedPaymentPresent = true)`
- Stage 6 transport profile switching:
  - `cloud` transport profile
  - `local` Bot API transport profile
  - `fake` mode profile for testkit-driven runs
- Stage 6 diagnostics listener wiring:
  - demo `BotApiRequestListener`
  - demo `BotApiResponseListener`
  - demo `UpdateProcessingListener`
  - demo `ErrorListener`
- Stage 3/4 compatibility scenarios are kept:
  - FSM `/startform`
  - callback `menu:*`
  - inline mode examples
  - invoice `/buy_test`
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

Optional for transport profile demos:

- `DEMO_CLOUD_BOTAPI_BASE_URL` (default `https://api.telegram.org`)
- `DEMO_LOCAL_BOTAPI_BASE_URL` (default `http://127.0.0.1:8081`)
- `DEMO_FAKE_BOTAPI_BASE_URL` (default `http://127.0.0.1:18081`)

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

For channel membership/admin commands:
- `DEMO_CHANNEL_CHAT_ID` can be used as default `chat_id` if command args are omitted.

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
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,cloud'
```

## Use Redis for FSM user state

```yaml
telegram:
  bot:
    state:
      storage: redis
      redis:
        key-prefix: telegram:fsm
        ttl-seconds: 86400

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## Run demo (polling + local Bot API)

```bash
export BOT_TOKEN=123456:ABCDEF
export DEMO_LOCAL_BOTAPI_BASE_URL=http://127.0.0.1:8081
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,local'
```

## Run demo (webhook + cloud transport)

```bash
export BOT_TOKEN=123456:ABCDEF
export BOT_WEBHOOK_PUBLIC_URL=https://example.com
export BOT_WEBHOOK_SECRET_TOKEN=super-secret
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=webhook,cloud'
```

Default webhook endpoint path: `/telegram/webhook`.

## Run demo in fake testkit mode (dev/test)

This mode is for local verification, not production runtime.  
`testkit` is used only in `testImplementation`.

```bash
./gradlew :telegram-bot-framework-demo:test --tests '*DemoFakeModeIntegrationTest'
```

The test starts an embedded `FakeBotApiServer`, points the demo context to it via `telegram.bot.transport.base-url`, and asserts outgoing Bot API requests.

## Diagnostics listener demo

Enable demo diagnostics listeners:

```yaml
demo:
  diagnostics:
    enabled: true
```

Listeners are regular Spring beans in:
- `/Users/sergej/Documents/telegalib/telegram-bot-framework-demo/src/main/java/ru/tardyon/botframework/telegram/demo/config/DemoDiagnosticsConfiguration.java`

## Notes

- Demo stays as usage example only; runtime logic remains in `core` and `starter`.
- No DB/Redis/Docker/MTProto/production billing logic is included.
- Demo does not depend on `botapi-generator`.
- `testkit` is not part of demo production runtime classpath.
