# telegram-bot-framework-testkit

Lightweight test support module for `telegram-bot-framework-core`.

## Included
- `FakeBotApiServer`: fake HTTP Bot API server with configurable method responses and request recording.
- `PollingUpdateSimulator`: deterministic `getUpdates` simulation via queued `Update` objects.
- `WebhookSimulator`: helper for POSTing webhook payloads (supports secret token header).
- `RequestAssertions`: framework-agnostic assertion helpers (`AssertionError` based).
- `TelegramJsonFixtures` / `UpdateFixtures`: canned JSON and builder-style test fixtures.

## Not included intentionally
- Full Telegram protocol emulation.
- Docker dependency.
- Timing/network chaos simulation.
