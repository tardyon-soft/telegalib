# telegram-bot-framework-demo

Spring Boot demo for `telegram-bot-framework-spring-boot-starter` with Stage 3 scenarios, using annotation-driven API.

## What demo shows

- Annotation-driven handlers:
  - `@BotController`
  - `@OnMessage`
  - `@OnCallbackQuery`
  - `@OnInlineQuery`
  - `@OnChosenInlineResult`
- FSM conversation:
  - `/startform` -> asks name -> asks language -> completes and clears state
- Advanced inline keyboard:
  - callback button `menu:*`
  - `switch_inline_query_current_chat`
  - `switch_inline_query_chosen_chat`
  - `copy_text`
- Callback handling:
  - `menu:*` -> `answer("OK")` + `editText(...)`
- Inline mode:
  - inline query answers with `InlineQueryResultArticle` + `InlineQueryResultPhoto`
  - chosen inline result logging example
- Media group:
  - `/albumtest` with local uploads or `file_id` sources
- Menu button:
  - `/menubutton-init` -> `setChatMenuButton` + `getChatMenuButton`
- Middleware:
  - `UpdateMiddleware` logs update type and latency

## Inline mode prerequisites

Before testing inline queries:

1. Enable inline mode for your bot in BotFather (`/setinline`).
2. Optionally enable inline feedback if you want richer chosen-result analytics in BotFather.
3. In Telegram chat, type `@your_bot_username <query>` to trigger inline query flow.

## Environment variables

Required:

- `BOT_TOKEN`

Optional for webhook mode:

- `BOT_WEBHOOK_PUBLIC_URL`
- `BOT_WEBHOOK_SECRET_TOKEN`

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

Webhook endpoint path by default: `/telegram/webhook`.

## Notes

- Demo is only usage example; library runtime remains in `core`/`starter`.
- No production deployment hardening is included.
