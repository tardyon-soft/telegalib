package ru.tardyon.botframework.telegram.api.model.story;

import ru.tardyon.botframework.telegram.api.file.InputFile;

public sealed interface InputStoryContent permits InputStoryContentPhoto, InputStoryContentVideo {

    String type();

    InputFile media();
}
