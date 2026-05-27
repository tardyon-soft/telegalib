# TelegramApiClient attachment examples

Ниже примеры для текущего API библиотеки `ru.tardyon.botframework.telegram`.

## Базовая настройка

```java
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.SendAnimationRequest;
import ru.tardyon.botframework.telegram.api.method.SendAudioRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.method.SendPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.SendVideoRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaAudio;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaDocument;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaVideo;
import ru.tardyon.botframework.telegram.api.model.media.MediaGroups;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaVideo;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext.UpdateType;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnScreenMessage;
import ru.tardyon.botframework.telegram.spring.boot.annotation.ScreenController;
```

```java
TelegramApiClient client = new DefaultTelegramApiClient(System.getenv("BOT_TOKEN"));
long chatId = 123456789L;
```

`InputFile` можно передавать четырьмя способами:

```java
InputFile.path(Path.of("/tmp/file.pdf"));
InputFile.fileId("BQACAgIAAxkBA...");
InputFile.url("https://example.com/file.pdf");
InputFile.bytes("hello.txt", "hello".getBytes(StandardCharsets.UTF_8));
```

## Одиночные вложения

### Фото

```java
client.sendPhoto(SendPhotoRequest.of(
    chatId,
    InputFile.path(Path.of("/tmp/photo.jpg"))
));
```

С подписью и HTML:

```java
client.sendPhoto(new SendPhotoRequest(
    chatId,
    InputFile.path(Path.of("/tmp/photo.jpg")),
    "<b>Фото</b>",
    "HTML",
    null
));
```

### Документ или любой файл

```java
client.sendDocument(SendDocumentRequest.of(
    chatId,
    InputFile.path(Path.of("/tmp/file.pdf"))
));
```

С подписью:

```java
client.sendDocument(new SendDocumentRequest(
    chatId,
    InputFile.path(Path.of("/tmp/file.pdf")),
    "Документ",
    null
));
```

### Видео

```java
client.sendVideo(SendVideoRequest.of(
    chatId,
    InputFile.path(Path.of("/tmp/video.mp4"))
));
```

С подписью:

```java
client.sendVideo(new SendVideoRequest(
    chatId,
    null,
    InputFile.path(Path.of("/tmp/video.mp4")),
    "Видео",
    null
));
```

### Аудио

```java
client.sendAudio(SendAudioRequest.of(
    chatId,
    InputFile.path(Path.of("/tmp/audio.mp3"))
));
```

С подписью:

```java
client.sendAudio(new SendAudioRequest(
    chatId,
    null,
    InputFile.path(Path.of("/tmp/audio.mp3")),
    "Аудио",
    null
));
```

### Animation / GIF

```java
client.sendAnimation(SendAnimationRequest.of(
    chatId,
    InputFile.path(Path.of("/tmp/animation.gif"))
));
```

С подписью:

```java
client.sendAnimation(new SendAnimationRequest(
    chatId,
    null,
    InputFile.path(Path.of("/tmp/animation.gif")),
    "GIF",
    null
));
```

## Список вложений: media group

`sendMediaGroup` отправляет альбом от 2 до 10 элементов.

Фото и видео можно смешивать:

```java
client.sendMediaGroup(SendMediaGroupRequest.of(
    chatId,
    List.of(
        InputMediaPhoto.of(InputFile.path(Path.of("/tmp/photo1.jpg"))),
        InputMediaPhoto.of(InputFile.path(Path.of("/tmp/photo2.jpg"))),
        InputMediaVideo.of(InputFile.path(Path.of("/tmp/video.mp4")))
    )
));
```

То же через builder:

```java
client.sendMediaGroup(SendMediaGroupRequest.of(
    chatId,
    MediaGroups.builder()
        .photo(InputFile.path(Path.of("/tmp/photo1.jpg")))
        .photo(InputFile.path(Path.of("/tmp/photo2.jpg")))
        .video(InputFile.path(Path.of("/tmp/video.mp4")))
        .build()
));
```

Документы можно отправлять альбомом только с документами:

```java
client.sendMediaGroup(SendMediaGroupRequest.of(
    chatId,
    List.of(
        InputMediaDocument.of(InputFile.path(Path.of("/tmp/a.pdf"))),
        InputMediaDocument.of(InputFile.path(Path.of("/tmp/b.zip")))
    )
));
```

Аудио можно отправлять альбомом только с аудио:

```java
client.sendMediaGroup(SendMediaGroupRequest.of(
    chatId,
    List.of(
        InputMediaAudio.of(InputFile.path(Path.of("/tmp/track1.mp3"))),
        InputMediaAudio.of(InputFile.path(Path.of("/tmp/track2.mp3")))
    )
));
```

Подписи в альбоме:

```java
client.sendMediaGroup(SendMediaGroupRequest.of(
    chatId,
    List.of(
        new InputMediaPhoto(
            "photo",
            InputFile.path(Path.of("/tmp/photo1.jpg")),
            "<b>Первое фото</b>",
            "HTML",
            null
        ),
        new InputMediaPhoto(
            "photo",
            InputFile.path(Path.of("/tmp/photo2.jpg")),
            "Второе фото",
            null,
            null
        )
    )
));
```

## Платные медиа

Платные медиа поддерживают фото и видео:

```java
client.sendPaidMedia(new SendPaidMediaRequest(
    null,
    chatId,
    100,
    List.of(
        InputPaidMediaPhoto.of(InputFile.path(Path.of("/tmp/paid-photo.jpg"))),
        InputPaidMediaVideo.of(InputFile.path(Path.of("/tmp/paid-video.mp4")))
    ),
    "order-or-user-payload-123",
    "Платный контент",
    null,
    null,
    null,
    null
));
```

## Обработка входящих вложений в @OnScreenMessage

У `Message` вложение лежит в одном из полей: `photo`, `document`, `video`, `audio`, `voice`, `animation`, `videoNote`, `sticker`, `paidMedia`.

```java
@ScreenController
@Component
public class AttachmentScreenController {

    @OnScreenMessage(screen = "upload")
    public ScreenAction onUpload(Message message, TelegramMessage telegramMessage) {
        if (message.photo() != null && !message.photo().isEmpty()) {
            var biggest = message.photo().get(message.photo().size() - 1);
            telegramMessage.reply("Получил фото: " + biggest.fileId());
            return ScreenAction.handled();
        }

        if (message.document() != null) {
            telegramMessage.reply("Получил документ: " + message.document().fileId());
            return ScreenAction.handled();
        }

        if (message.video() != null) {
            telegramMessage.reply("Получил видео: " + message.video().fileId());
            return ScreenAction.handled();
        }

        if (message.audio() != null) {
            telegramMessage.reply("Получил аудио: " + message.audio().fileId());
            return ScreenAction.handled();
        }

        if (message.voice() != null) {
            telegramMessage.reply("Получил voice: " + message.voice().fileId());
            return ScreenAction.handled();
        }

        if (message.animation() != null) {
            telegramMessage.reply("Получил animation/gif: " + message.animation().fileId());
            return ScreenAction.handled();
        }

        if (message.videoNote() != null) {
            telegramMessage.reply("Получил video note: " + message.videoNote().fileId());
            return ScreenAction.handled();
        }

        if (message.sticker() != null) {
            telegramMessage.reply("Получил стикер: " + message.sticker().fileId());
            return ScreenAction.handled();
        }

        return ScreenAction.unhandled();
    }
}
```

## Обработка альбома, который пришел от пользователя

Telegram присылает альбом не одним сообщением, а несколькими `Message` с одинаковым `mediaGroupId`.
Поэтому в `@OnScreenMessage` нужно складывать элементы альбома в буфер и обрабатывать после короткой паузы.

```java
@Service
public class AlbumCollector {
    private final Map<String, List<Message>> albums = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void add(Message message, Duration quietPeriod, java.util.function.Consumer<List<Message>> onComplete) {
        if (message.chat() == null || message.mediaGroupId() == null) {
            onComplete.accept(List.of(message));
            return;
        }

        String key = message.chat().id() + ":" + message.mediaGroupId();
        albums.computeIfAbsent(key, ignored -> new CopyOnWriteArrayList<>()).add(message);

        scheduler.schedule(() -> {
            List<Message> collected = albums.remove(key);
            if (collected != null && !collected.isEmpty()) {
                onComplete.accept(List.copyOf(collected));
            }
        }, quietPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }
}
```

```java
@ScreenController
@Component
public class AlbumScreenController {
    private final AlbumCollector albumCollector;

    public AlbumScreenController(AlbumCollector albumCollector) {
        this.albumCollector = albumCollector;
    }

    @OnScreenMessage(screen = "upload")
    public ScreenAction onUpload(Message message, TelegramMessage telegramMessage) {
        albumCollector.add(message, Duration.ofMillis(1200), album -> {
            List<String> fileIds = album.stream()
                .map(this::attachmentFileId)
                .filter(id -> id != null && !id.isBlank())
                .toList();

            telegramMessage.reply("Получил вложений: " + fileIds.size());
        });

        return ScreenAction.handled();
    }

    private String attachmentFileId(Message message) {
        if (message.photo() != null && !message.photo().isEmpty()) {
            return message.photo().get(message.photo().size() - 1).fileId();
        }
        if (message.document() != null) {
            return message.document().fileId();
        }
        if (message.video() != null) {
            return message.video().fileId();
        }
        if (message.audio() != null) {
            return message.audio().fileId();
        }
        if (message.voice() != null) {
            return message.voice().fileId();
        }
        if (message.animation() != null) {
            return message.animation().fileId();
        }
        if (message.videoNote() != null) {
            return message.videoNote().fileId();
        }
        if (message.sticker() != null) {
            return message.sticker().fileId();
        }
        return null;
    }
}
```

Для production лучше добавить TTL-очистку и shutdown для `ScheduledExecutorService`.

## Отслеживание публикации и редактирования постов

В `Update` есть четыре основные ветки сообщений:

```java
update.message();            // новое сообщение в обычном чате
update.editedMessage();      // редактирование сообщения в обычном чате
update.channelPost();        // новый пост в канале
update.editedChannelPost();  // редактирование поста в канале
```

В screen-обработчике можно принимать `UpdateContext` и смотреть `getUpdateType()`.
Такой обработчик сработает для активного screen, если событие относится к текущему chat/screen key.

```java
@ScreenController
@Component
public class PostTrackingScreenController {

    @OnScreenMessage(screen = "post_monitor")
    public ScreenAction onPostEvent(UpdateContext context, Message message, TelegramMessage telegramMessage) {
        if (context.getUpdateType() == UpdateType.CHANNEL_POST) {
            telegramMessage.reply("Опубликован пост: " + describe(message));
            return ScreenAction.handled();
        }

        if (context.getUpdateType() == UpdateType.EDITED_CHANNEL_POST) {
            telegramMessage.reply("Отредактирован пост: " + describe(message));
            return ScreenAction.handled();
        }

        if (context.getUpdateType() == UpdateType.MESSAGE) {
            telegramMessage.reply("Новое сообщение: " + describe(message));
            return ScreenAction.handled();
        }

        if (context.getUpdateType() == UpdateType.EDITED_MESSAGE) {
            telegramMessage.reply("Отредактировано сообщение: " + describe(message));
            return ScreenAction.handled();
        }

        return ScreenAction.unhandled();
    }

    private String describe(Message message) {
        if (message.text() != null) {
            return message.text();
        }
        if (message.caption() != null) {
            return message.caption();
        }
        if (message.photo() != null) {
            return "photo";
        }
        if (message.document() != null) {
            return "document " + message.document().fileName();
        }
        if (message.video() != null) {
            return "video";
        }
        return "message_id=" + message.messageId();
    }
}
```

Если нужно отслеживать публикации глобально, не только внутри screen, удобнее делать это через middleware:

```java
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddlewareChain;

@Component
public class PostTrackingMiddleware implements UpdateMiddleware {

    @Override
    public void handle(UpdateContext context, UpdateMiddlewareChain chain) {
        Message message = context.getMessage();

        if (message != null) {
            switch (context.getUpdateType()) {
                case MESSAGE -> onPublishedMessage(message);
                case EDITED_MESSAGE -> onEditedMessage(message);
                case CHANNEL_POST -> onPublishedChannelPost(message);
                case EDITED_CHANNEL_POST -> onEditedChannelPost(message);
                default -> {
                    // not a post/message update
                }
            }
        }

        chain.proceed(context);
    }

    private void onPublishedMessage(Message message) {
        // сохранить в БД, залогировать, запустить workflow
    }

    private void onEditedMessage(Message message) {
        // обновить сохраненную версию сообщения
    }

    private void onPublishedChannelPost(Message message) {
        // обработать публикацию в канале
    }

    private void onEditedChannelPost(Message message) {
        // обработать редактирование поста в канале
    }
}
```

## Allowed updates

Чтобы Telegram присылал редактирования и channel posts, убедись, что они не отфильтрованы в `allowed_updates`.
Для long polling:

```java
client.getUpdates(new GetUpdatesRequest(
    null,
    100,
    30,
    List.of(
        "message",
        "edited_message",
        "channel_post",
        "edited_channel_post"
    )
));
```

В Spring Boot starter то же самое обычно задается в конфигурации:

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: polling
    polling:
      allowed-updates:
        - message
        - edited_message
        - channel_post
        - edited_channel_post
```

Для webhook эти значения уходят в `SetWebhookRequest.allowedUpdates`.
