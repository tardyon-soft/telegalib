package ru.tardyon.botframework.telegram.api.file;

import java.nio.file.Path;
import java.util.Objects;

public record InputFilePath(
    Path path,
    String filename,
    String contentType
) implements InputFile {

    public InputFilePath {
        Objects.requireNonNull(path, "path must not be null");
    }

    public String resolvedFilename() {
        if (filename != null && !filename.isBlank()) {
            return filename;
        }
        Path fileName = path.getFileName();
        return fileName == null ? "upload.bin" : fileName.toString();
    }
}
