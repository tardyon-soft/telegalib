# telegram-bot-framework-spring-boot-starter

Thin Spring Boot integration layer over `telegram-bot-framework-core`.

## What it configures

- `TelegramApiClient`
- `LongPollingOptions`
- `LongPollingRunner`
- `Router`
- `Dispatcher`
- `WebhookUpdateProcessor`
- `TelegramWebhookController` (when servlet web app + `telegram.bot.webhook.enabled=true`)
- `TelegramBot`
- `TelegramBotLifecycle` (mode-aware start/stop)

## Polling mode (`application.yml`)

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: polling
    polling:
      enabled: true
      timeout: 30
      limit: 100
      allowed-updates:
        - message
        - callback_query
```

## Webhook mode (`application.yml`)

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: webhook
    webhook:
      enabled: true
      path: /telegram/webhook
      public-url: https://example.com
      secret-token: ${BOT_WEBHOOK_SECRET}
      drop-pending-updates: true
```

## Usage

1. Define handlers via `Router` bean in your app.
2. Optionally provide one or more `UpdateMiddleware` beans.
3. Choose `telegram.bot.mode`:
   - `polling`: starter starts core long polling runtime.
   - `webhook`: starter exposes webhook endpoint and starts bot in webhook mode.
4. If `webhook.public-url` is configured, starter calls `setWebhook` on startup.
