package ru.tardyon.botframework.telegram.demo.handler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultPhoto;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.api.model.markup.SwitchInlineQueryChosenChat;
import ru.tardyon.botframework.telegram.api.model.media.MediaGroups;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButtons;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.spring.boot.annotation.BotController;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnCallbackQuery;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnChosenInlineResult;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnInlineQuery;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnMessage;

@BotController
public class Stage3DemoController {

    private static final String STATE_AWAITING_NAME = "form.awaiting_name";
    private static final String STATE_AWAITING_LANGUAGE = "form.awaiting_language";

    private final TelegramApiClient telegramApiClient;

    public Stage3DemoController(TelegramApiClient telegramApiClient) {
        this.telegramApiClient = telegramApiClient;
    }

    @OnMessage(command = "start")
    public void onStart(TelegramMessage telegramMessage) {
        InlineKeyboardMarkup inlineKeyboard = Keyboards.inlineKeyboard()
            .row(
                Keyboards.callbackButton("Меню", "menu:main"),
                Keyboards.switchInlineQueryCurrentChatButton("Inline here", "demo ")
            )
            .row(
                Keyboards.switchInlineQueryChosenChatButton(
                    "Inline in chosen chat",
                    new SwitchInlineQueryChosenChat("demo ", true, false, true, false)
                ),
                Keyboards.copyTextButton("Copy help", "/startform")
            )
            .build();

        telegramMessage.reply(
            "Stage 3 demo:\n" +
                "/startform - FSM диалог\n" +
                "/commands-init - регистрация команд\n" +
                "/albumtest - media group\n" +
                "/menubutton-init - menu button\n" +
                "Нажми inline-кнопки ниже для inline mode сценариев.",
            inlineKeyboard
        );
    }

    @OnMessage(command = "commands-init")
    public void onCommandsInit(TelegramMessage telegramMessage) {
        telegramApiClient.setMyCommands(
            new SetMyCommandsRequest(
                List.of(
                    new BotCommand("start", "Stage 3 demo menu"),
                    new BotCommand("startform", "Start FSM form"),
                    new BotCommand("albumtest", "Send media group album"),
                    new BotCommand("menubutton-init", "Configure chat menu button")
                ),
                null,
                null
            )
        );
        telegramMessage.reply("Команды зарегистрированы.");
    }

    @OnMessage(command = "startform")
    public void onStartForm(UpdateContext context, TelegramMessage telegramMessage) {
        context.state().clear();
        context.state().set(STATE_AWAITING_NAME);
        telegramMessage.reply("Шаг 1/2: отправь свое имя.");
    }

    @OnMessage(state = STATE_AWAITING_NAME)
    public void onFormName(UpdateContext context, Message message, TelegramMessage telegramMessage) {
        if (!StringUtils.hasText(message.text())) {
            telegramMessage.reply("Нужно текстовое имя. Попробуй еще раз.");
            return;
        }
        context.state().data().put("name", message.text().trim());
        context.state().set(STATE_AWAITING_LANGUAGE);
        telegramMessage.reply("Шаг 2/2: отправь предпочитаемый язык (например, Java).");
    }

    @OnMessage(state = STATE_AWAITING_LANGUAGE)
    public void onFormLanguage(UpdateContext context, Message message, TelegramMessage telegramMessage) {
        if (!StringUtils.hasText(message.text())) {
            telegramMessage.reply("Нужен текст. Попробуй еще раз.");
            return;
        }
        String name = context.state().data().get("name", String.class).orElse("unknown");
        String language = message.text().trim();
        telegramMessage.reply("Форма завершена. Имя: " + name + ", язык: " + language);
        context.state().clear();
    }

    @OnCallbackQuery(callbackPrefix = "menu:")
    public void onMenuCallback(TelegramCallbackQuery callback, CallbackQuery callbackQuery) {
        callback.answer("OK");
        if (callback.message() != null) {
            callback.message().editText("Callback обработан: " + callbackQuery.data());
        }
    }

    @OnMessage(command = "albumtest")
    public void onAlbumTest(Message message, TelegramMessage telegramMessage) {
        String localFirst = System.getenv("DEMO_ALBUM_FILE1");
        String localSecond = System.getenv("DEMO_ALBUM_FILE2");

        try {
            if (StringUtils.hasText(localFirst) && StringUtils.hasText(localSecond)) {
                Path first = Path.of(localFirst);
                Path second = Path.of(localSecond);
                if (!Files.isRegularFile(first) || !Files.isRegularFile(second)) {
                    telegramMessage.reply("DEMO_ALBUM_FILE1/2 должны указывать на существующие файлы.");
                    return;
                }
                telegramApiClient.sendMediaGroup(
                    SendMediaGroupRequest.of(
                        message.chat().id(),
                        MediaGroups.builder()
                            .photo(InputFile.path(first))
                            .photo(InputFile.path(second))
                            .build()
                    )
                );
                telegramMessage.reply("Отправлен media group из локальных файлов.");
                return;
            }

            String fileId1 = System.getenv("DEMO_ALBUM_FILE_ID1");
            String fileId2 = System.getenv("DEMO_ALBUM_FILE_ID2");
            if (StringUtils.hasText(fileId1) && StringUtils.hasText(fileId2)) {
                telegramApiClient.sendMediaGroup(
                    SendMediaGroupRequest.of(
                        message.chat().id(),
                        MediaGroups.builder()
                            .photo(InputFile.fileId(fileId1))
                            .photo(InputFile.fileId(fileId2))
                            .build()
                    )
                );
                telegramMessage.reply("Отправлен media group из file_id.");
                return;
            }
        } catch (RuntimeException e) {
            telegramMessage.reply("Ошибка отправки album: " + e.getMessage());
            return;
        }

        telegramMessage.reply(
            "Для /albumtest укажи либо:\n" +
                "1) DEMO_ALBUM_FILE1 и DEMO_ALBUM_FILE2 (локальные файлы),\n" +
                "или\n" +
                "2) DEMO_ALBUM_FILE_ID1 и DEMO_ALBUM_FILE_ID2 (Telegram file_id)."
        );
    }

    @OnMessage(command = "menubutton-init")
    public void onMenuButtonInit(Message message, TelegramMessage telegramMessage) {
        telegramApiClient.setChatMenuButton(
            new SetChatMenuButtonRequest(
                message.chat().id(),
                MenuButtons.commandsButton()
            )
        );
        var menuButton = telegramApiClient.getChatMenuButton(new GetChatMenuButtonRequest(message.chat().id()));
        telegramMessage.reply("Menu button configured. Current type: " + menuButton.type());
    }

    @OnInlineQuery
    public void onInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.query() == null ? "" : inlineQuery.query().trim();

        List<InlineQueryResult> results = List.of(
            new InlineQueryResultArticle(
                "demo-article-" + inlineQuery.id(),
                "Article: " + (query.isEmpty() ? "empty query" : query),
                InputTextMessageContent.of("Inline article selected: " + (query.isEmpty() ? "empty query" : query))
            ),
            new InlineQueryResultPhoto(
                "demo-photo-" + inlineQuery.id(),
                "https://picsum.photos/seed/telegalib/800/600",
                "https://picsum.photos/seed/telegalib/200/200"
            )
        );

        String nextOffset = query.isEmpty() ? "page:1" : "";
        telegramApiClient.answerInlineQuery(
            new AnswerInlineQueryRequest(
                inlineQuery.id(),
                results,
                10,
                true,
                nextOffset,
                null
            )
        );
    }

    @OnChosenInlineResult
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) {
        System.out.println(
            "[demo-inline] chosen result_id=" + chosenInlineResult.resultId()
                + " query=" + chosenInlineResult.query()
                + " from=" + (chosenInlineResult.from() != null ? chosenInlineResult.from().id() : "unknown")
        );
    }
}
