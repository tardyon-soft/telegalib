# telegram-bot-framework-demo

Spring Boot demo for `telegram-bot-framework-spring-boot-starter` with Stage 4 scenarios using annotation-driven API.

## What demo shows

- Annotation-driven handlers:
  - `@BotController`
  - `@OnMessage`
  - `@OnCallbackQuery`
  - `@OnInlineQuery`
  - `@OnChosenInlineResult`
  - `@OnShippingQuery`
  - `@OnPreCheckoutQuery`
  - `@OnBusinessConnection`
  - `@OnBusinessMessage`
  - `@OnDeletedBusinessMessages`
- Payments:
  - `/buy-test` -> `sendInvoice`
  - `shipping_query` -> `answerShippingQuery`
  - `pre_checkout_query` -> `answerPreCheckoutQuery`
- Mini Apps:
  - `/webapp` -> reply keyboard with `web_app` button
  - incoming service message with `web_app_data` handled by `@OnMessage(webAppDataPresent = true)`
  - `/prepared-inline-test` -> `savePreparedInlineMessage`
- Business updates:
  - `business_connection` logging example
  - `business_message` -> `readBusinessMessage` + demo reply via `business_connection_id`
  - `deleted_business_messages` logging example
- Stage 3 compatibility scenarios kept:
  - FSM `/startform`
  - callback `menu:*`
  - inline mode examples
  - media group `/albumtest`
  - menu button `/menubutton-init`

## Stage 4 prerequisites

Payments:

1. For regular invoice flow, configure provider token in BotFather and set `PAYMENT_PROVIDER_TOKEN`.
2. For Telegram Stars demo, set `DEMO_STARS_MODE=true` (uses currency `XTR` in this demo).

Mini Apps:

1. Configure Mini App URL in BotFather for your bot.
2. Set `DEMO_WEB_APP_URL` to your Mini App HTTPS URL.

Business:

1. Business updates require linked business connection and corresponding bot rights.
2. Without business connection, business handlers may not receive updates.

Inline mode:

1. Enable inline mode for your bot in BotFather (`/setinline`).
2. Optionally enable inline feedback for chosen-result analytics.

## Environment variables

Required:

- `BOT_TOKEN`

Optional for polling/webhook:

- `BOT_WEBHOOK_PUBLIC_URL`
- `BOT_WEBHOOK_SECRET_TOKEN`

Optional for Stage 4 demos:

- `PAYMENT_PROVIDER_TOKEN`
- `DEMO_STARS_MODE` (`true` for Stars invoice flow)
- `DEMO_WEB_APP_URL`

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
- No DB/Redis/Docker/production billing logic/secret vaulting is included.
