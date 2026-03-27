package ru.tardyon.botframework.telegram.api.file;

import java.io.InputStream;
import java.util.Objects;

public record InputFileStream(
    String filename,
    InputStream inputStream,
    String contentType
) implements InputFile {

    public InputFileStream {
        filename = InputFile.requireText(filename, "filename");
        Objects.requireNonNull(inputStream, "inputStream must not be null");
    }
}
