# Bot API Generator (Stage 6 MVP)

This module is tooling-only and **must not** be used as a production runtime dependency.

## Scope

Uses official Telegram docs as source-of-truth context:
- https://core.telegram.org/bots/api
- https://core.telegram.org/bots/api-changelog

MVP generator pipeline stages:
1. input acquisition (`BotApiSchemaInputLoader`)
2. parsing to intermediate model (`BotApiSubsetSchemaParser`)
3. code writing (`JavaSubsetCodeWriter`)

## What Is Generated

Generated subset targets (proof-of-viability):
- DTO records (subset): `GeneratedUser`, `GeneratedChat`, `GeneratedMessage`
- method request records (subset):
  - `GeneratedGetUpdatesRequest`
  - `GeneratedSendMessageRequest`
  - `GeneratedDeleteMessageRequest`
- method catalog mapping: `GeneratedBotApiMethodCatalog`

Output directory:
- `build/generated/sources/botapi/java`

Generated files are explicitly marked with:
- `GENERATED FROM TELEGRAM BOT API SUBSET SCHEMA`

## What Stays Manual

Not generated in MVP:
- runtime client logic (`DefaultTelegramApiClient`)
- dispatcher/router/middleware/FSM
- starter/demo integration
- wrappers/high-level runtime API

## Safe Regeneration

1. Review/update schema input file:
   - `src/main/resources/botapi/subset-schema.json`
2. Run generator task.
3. Review diffs in generated output.
4. Copy selected changes into handwritten runtime modules only after manual review.

