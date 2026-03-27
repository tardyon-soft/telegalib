# telegram-bot-framework-demo

Spring Boot demo for `telegram-bot-framework-spring-boot-starter` with Stage 2 examples.

## What demo shows

- `/start` -> reply with:
  - inline keyboard (`menu:one`, `menu:two`)
  - reply keyboard (`ping`, `/commands-init`, `/file-test`)
- `ping` -> reply `pong`
- callback data `menu:*` -> `answer("OK")` + `editReplyMarkup(...)`
- `/commands-init` -> `setMyCommands(...)`
- `/file-test <file_id>` -> `getFile` + `downloadFile` + `downloadFile(..., target)`
- `/file-test send` -> `sendDocument` from local file (`DEMO_UPLOAD_FILE`)
- middleware example:
  - `UpdateMiddleware` logs update type and elapsed time to stdout

## Requirements

- Java 21
- `BOT_TOKEN` environment variable

Optional (webhook mode):
- `BOT_WEBHOOK_PUBLIC_URL`
- `BOT_WEBHOOK_SECRET_TOKEN`

Optional (file upload demo):
- `DEMO_UPLOAD_FILE` (absolute path to local file)

## Run in Polling mode

```bash
export BOT_TOKEN=123456:ABCDEF
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling'
```

## Run in Webhook mode

```bash
export BOT_TOKEN=123456:ABCDEF
export BOT_WEBHOOK_PUBLIC_URL=https://example.com
export BOT_WEBHOOK_SECRET_TOKEN=super-secret
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=webhook'
```

Webhook endpoint path by default: `/telegram/webhook`.

## Notes

- Demo keeps all runtime logic in core/starter and only wires usage examples.
- This module is not production deployment guidance.
