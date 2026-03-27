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
- `TelegramMonetizationOperations` (thin delegating helper over `TelegramApiClient`)
- `TelegramBusinessOperations` (thin delegating helper over `TelegramApiClient`)

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

## Stage 5 annotation examples

```java
@BotController
class MonetizationController {

    @OnMessage(giftPresent = true)
    public void onGiftService(ru.tardyon.botframework.telegram.api.model.payment.GiftInfo giftInfo) {
        // react to service message with gift payload
    }

    @OnBusinessMessage(refundedPaymentPresent = true)
    public void onBusinessRefund(ru.tardyon.botframework.telegram.api.model.payment.RefundedPayment refundedPayment) {
        // react to business service refund message
    }
}
```

## Stage 5 business operations bean usage

```java
@Component
class BusinessOpsRunner {
    private final TelegramBusinessOperations businessOps;

    BusinessOpsRunner(TelegramBusinessOperations businessOps) {
        this.businessOps = businessOps;
    }

    void upgradeGift() {
        businessOps.upgradeGift(new UpgradeGiftRequest("bc-1", "owned-gift-id", true, 0));
    }
}
```

## Manual Router + starter coexistence

```java
@Configuration
class BotRoutingConfig {
    @Bean
    Router customRouter() {
        Router router = new Router();
        router.message(Filters.command("start"), (ctx, msg) -> ctx.telegramMessage().reply("Hello from manual router"));
        return router;
    }
}
```
