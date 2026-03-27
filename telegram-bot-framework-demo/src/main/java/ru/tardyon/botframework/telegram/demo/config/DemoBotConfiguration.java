package ru.tardyon.botframework.telegram.demo.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.method.GetFileRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyKeyboardMarkup;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

@Configuration
public class DemoBotConfiguration {

    @Bean
    public Router demoRouter(TelegramApiClient telegramApiClient) {
        Router router = new Router();
        InlineKeyboardMarkup startInlineKeyboard = Keyboards.inlineKeyboard()
            .row(
                Keyboards.callbackButton("Меню 1", "menu:one"),
                Keyboards.callbackButton("Меню 2", "menu:two")
            )
            .build();
        ReplyKeyboardMarkup replyKeyboard = Keyboards.replyKeyboard()
            .row("ping", "/commands-init")
            .row("/file-test")
            .build();

        router.message(Filters.command("start"), (ctx, message) -> {
            ctx.telegramMessage().reply("Привет! Выбери действие в inline-клавиатуре:", startInlineKeyboard);
            ctx.telegramMessage().reply("Или используй кнопки reply keyboard ниже.", replyKeyboard);
        });

        router.message(Filters.textEquals("ping"), (ctx, message) -> {
            ctx.telegramMessage().reply("pong");
        });

        router.message(Filters.command("commands-init"), (ctx, message) -> {
            boolean result = telegramApiClient.setMyCommands(
                new SetMyCommandsRequest(
                    List.of(
                        new BotCommand("start", "Показать demo keyboards"),
                        new BotCommand("commands-init", "Зарегистрировать команды"),
                        new BotCommand("file-test", "Файловый demo: /file-test <file_id> или /file-test send")
                    ),
                    null,
                    null
                )
            );
            ctx.telegramMessage().reply(result ? "Команды зарегистрированы." : "Не удалось зарегистрировать команды.");
        });

        router.message(Filters.command("file-test"), (ctx, message) -> {
            String args = ctx.commandContext().map(command -> command.argsRaw()).orElse("").trim();
            if (!StringUtils.hasText(args)) {
                ctx.telegramMessage().reply(
                    "Использование:\n" +
                        "/file-test <file_id> - getFile + download\n" +
                        "/file-test send - отправить локальный файл из DEMO_UPLOAD_FILE"
                );
                return;
            }

            if ("send".equalsIgnoreCase(args)) {
                String uploadPathValue = System.getenv("DEMO_UPLOAD_FILE");
                if (!StringUtils.hasText(uploadPathValue)) {
                    ctx.telegramMessage().reply("Для send укажи env DEMO_UPLOAD_FILE=/abs/path/to/file");
                    return;
                }
                Path uploadPath = Path.of(uploadPathValue);
                if (!Files.exists(uploadPath) || !Files.isRegularFile(uploadPath)) {
                    ctx.telegramMessage().reply("Файл из DEMO_UPLOAD_FILE не найден: " + uploadPath);
                    return;
                }
                telegramApiClient.sendDocument(
                    new SendDocumentRequest(
                        message.chat().id(),
                        InputFile.path(uploadPath),
                        "Demo upload from local file",
                        null
                    )
                );
                return;
            }

            String fileId = args.split("\\s+", 2)[0];
            var file = telegramApiClient.getFile(new GetFileRequest(fileId));
            if (!StringUtils.hasText(file.filePath())) {
                ctx.telegramMessage().reply("getFile вернул file_path=null, скачивание недоступно.");
                return;
            }

            byte[] bytes = telegramApiClient.downloadFile(file.filePath());
            Path target = Path.of("build", "demo-downloads", file.fileId() + ".bin");
            Path saved = telegramApiClient.downloadFile(file.filePath(), target);
            String downloadUrl = telegramApiClient.buildFileDownloadUrl(file.filePath());
            ctx.telegramMessage().reply(
                "File info:\n" +
                    "file_id: " + file.fileId() + "\n" +
                    "file_path: " + file.filePath() + "\n" +
                    "download_url: " + downloadUrl + "\n" +
                    "bytes: " + bytes.length + "\n" +
                    "saved_to: " + saved.toAbsolutePath()
            );
        });

        router.callbackQuery(Filters.callbackDataStartsWith("menu:"), (ctx, callbackQuery) -> {
            ctx.telegramCallbackQuery().answer("OK");
            if (ctx.telegramCallbackQuery().message() != null) {
                InlineKeyboardMarkup afterClick = Keyboards.inlineKeyboard()
                    .row(Keyboards.callbackButton("Назад в меню", "menu:root"))
                    .build();
                ctx.telegramCallbackQuery().message().editReplyMarkup(afterClick);
            }
        });

        return router;
    }

    @Bean
    @Order(0)
    public UpdateMiddleware demoLoggingMiddleware() {
        return (updateContext, chain) -> {
            long startedAtNanos = System.nanoTime();
            updateContext.setAttribute("demo.startedAtNanos", startedAtNanos);
            try {
                chain.proceed(updateContext);
            } finally {
                long elapsedMicros = (System.nanoTime() - startedAtNanos) / 1_000L;
                System.out.println(
                    "[demo-middleware] updateType=" + updateContext.getUpdateType() + " elapsedMicros=" + elapsedMicros
                );
            }
        };
    }
}
