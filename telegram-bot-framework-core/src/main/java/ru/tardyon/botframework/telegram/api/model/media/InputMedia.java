package ru.tardyon.botframework.telegram.api.model.media;

import java.util.List;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public sealed interface InputMedia permits InputMediaPhoto, InputMediaVideo, InputMediaDocument, InputMediaAudio {

    String type();

    InputFile media();

    String caption();

    String parseMode();

    List<MessageEntity> captionEntities();
}
