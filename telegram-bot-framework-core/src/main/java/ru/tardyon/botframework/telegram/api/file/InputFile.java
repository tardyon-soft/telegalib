package ru.tardyon.botframework.telegram.api.file;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public sealed interface InputFile permits InputFileReference, InputFileBytes, InputFilePath, InputFileStream {

    static InputFile fileId(String fileId) {
        return InputFileReference.fileId(fileId);
    }

    static InputFile url(String url) {
        return InputFileReference.url(url);
    }

    static InputFile bytes(String filename, byte[] content) {
        return new InputFileBytes(filename, content, null);
    }

    static InputFile bytes(String filename, byte[] content, String contentType) {
        return new InputFileBytes(filename, content, contentType);
    }

    static InputFile path(Path path) {
        return new InputFilePath(path, null, null);
    }

    static InputFile path(Path path, String filename, String contentType) {
        return new InputFilePath(path, filename, contentType);
    }

    static InputFile stream(String filename, InputStream inputStream) {
        return new InputFileStream(filename, inputStream, null);
    }

    static InputFile stream(String filename, InputStream inputStream, String contentType) {
        return new InputFileStream(filename, inputStream, contentType);
    }

    default boolean isUpload() {
        return this instanceof InputFileBytes || this instanceof InputFilePath || this instanceof InputFileStream;
    }

    default String asReference() {
        if (this instanceof InputFileReference reference) {
            return reference.value();
        }
        throw new IllegalStateException("InputFile is not a string reference");
    }

    static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
