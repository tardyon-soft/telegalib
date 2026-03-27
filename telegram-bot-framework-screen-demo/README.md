# telegram-bot-framework-screen-demo

Отдельный demo-модуль для экранного API (`screen layer`) поверх `telegram-bot-framework-core`.

## Что показывает demo

- один активный экран на чат;
- стек экранов (`push/back`);
- композицию экрана из виджетов;
- разделение `user state` и `screen state`.

## Команды

- `/screen_start` — открыть экран HOME;
- `/user_state_set` — записать `preferred_theme` в user state;
- `/user_state_show` — показать значение из user state.

## Запуск

```bash
BOT_TOKEN=<your_token> ./gradlew :telegram-bot-framework-screen-demo:bootRun
```

## Примечание

`screen state` хранится отдельно от FSM user state:
- user state: `UpdateContext.state()`
- screen state: `ScreenStateStorage`
