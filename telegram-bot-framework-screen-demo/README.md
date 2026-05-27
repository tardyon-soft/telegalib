# telegram-bot-framework-screen-demo

Отдельный demo-модуль для экранного API (`screen layer`) поверх `telegram-bot-framework-core` и `spring-boot-starter`.

## Что показывает demo

- один активный экран на чат;
- стек экранов (`push/back`);
- композицию экрана из виджетов;
- разделение `user state` и `screen state`.
- annotation-driven screen API (`@ScreenController`, `@Screen`, `@OnScreenMessage`, `@OnScreenCallback`).
- annotation-driven widget API (`@WidgetController`, `@Widget`, `@OnWidgetAction`).
- автокнопку `Назад` через `@Screen(addBackButton = true)` для всех non-main экранов.
- экран мониторинга фоновой задачи, который обновляется через `ScreenNavigator.renderCurrent()` раз в 5 секунд.

## Команды

- `/screen_start` — открыть экран HOME;
- `/task_monitor` — открыть экран фонового мониторинга задачи;
- `/user_state_set` — записать `preferred_theme` в user state;
- `/user_state_show` — показать значение из user state.
- в HOME есть переход на экран `catalog_list`, где список каналов строится виджетом;
- нажатие на канал передает `channel_id` и открывает `catalog_details`;
- экран деталей показывает название, описание, подписчиков, средние просмотры и URL картинки.
- экран `task_monitor` стартует demo-задачу, обновляет прогресс каждые 5 секунд и останавливает фоновой тик при завершении, уходе назад или нажатии `Остановить мониторинг`.

## Запуск

```bash
BOT_TOKEN=<your_token> ./gradlew :telegram-bot-framework-screen-demo:bootRun
```

## Примечание

`screen state` хранится отдельно от FSM user state:
- user state: `UpdateContext.state()`
- screen state: `ScreenStateStorage`

Экраны и переходы описаны в:
- `ScreenDemoScreensController` (аннотации screen API)
- `ScreenDemoRoutesController` (обычные команды для user state)
- `ScreenDemoWidgetsController` (виджеты и widget-actions)
