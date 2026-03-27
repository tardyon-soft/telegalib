package ru.tardyon.botframework.telegram.api.file;

import java.util.Arrays;

public record InputFileBytes(
    String filename,
    byte[] content,
    String contentType
) implements InputFile {

    public InputFileBytes {
        filename = InputFile.requireText(filename, "filename");
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
        content = Arrays.copyOf(content, content.length);
    }
}
