package ru.tardyon.botframework.telegram.api.file;

public record InputFileReference(
    String value,
    ReferenceType referenceType
) implements InputFile {

    public InputFileReference {
        value = InputFile.requireText(value, "value");
    }

    public static InputFileReference fileId(String fileId) {
        return new InputFileReference(fileId, ReferenceType.FILE_ID);
    }

    public static InputFileReference url(String url) {
        return new InputFileReference(url, ReferenceType.URL);
    }

    public enum ReferenceType {
        FILE_ID,
        URL
    }
}
