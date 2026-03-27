package ru.tardyon.botframework.telegram.api.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class MultipartFormData {

    private static final String CRLF = "\r\n";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final List<Part> parts = new ArrayList<>();

    public MultipartFormData addField(String name, String value) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(value, "value must not be null");
        parts.add(new FieldPart(name, value));
        return this;
    }

    public MultipartFormData addFile(String name, String filename, String contentType, byte[] content) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(filename, "filename must not be null");
        Objects.requireNonNull(content, "content must not be null");
        String actualContentType = (contentType == null || contentType.isBlank()) ? DEFAULT_CONTENT_TYPE : contentType;
        parts.add(new FilePart(name, filename, actualContentType, content));
        return this;
    }

    public BuiltMultipart build() {
        String boundary = "----telegalib-" + UUID.randomUUID();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            for (Part part : parts) {
                output.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
                part.writeTo(output);
                output.write(CRLF.getBytes(StandardCharsets.UTF_8));
            }
            output.write(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build multipart/form-data payload", e);
        }
        return new BuiltMultipart("multipart/form-data; boundary=" + boundary, output.toByteArray());
    }

    public record BuiltMultipart(String contentType, byte[] body) {
        public BuiltMultipart {
            Objects.requireNonNull(contentType, "contentType must not be null");
            Objects.requireNonNull(body, "body must not be null");
        }
    }

    private sealed interface Part permits FieldPart, FilePart {
        void writeTo(ByteArrayOutputStream output) throws IOException;
    }

    private record FieldPart(String name, String value) implements Part {
        @Override
        public void writeTo(ByteArrayOutputStream output) throws IOException {
            output.write(("Content-Disposition: form-data; name=\"" + name + "\"" + CRLF).getBytes(StandardCharsets.UTF_8));
            output.write(CRLF.getBytes(StandardCharsets.UTF_8));
            output.write(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private record FilePart(String name, String filename, String contentType, byte[] content) implements Part {
        @Override
        public void writeTo(ByteArrayOutputStream output) throws IOException {
            output.write(
                ("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"" + CRLF)
                    .getBytes(StandardCharsets.UTF_8)
            );
            output.write(("Content-Type: " + contentType + CRLF).getBytes(StandardCharsets.UTF_8));
            output.write(CRLF.getBytes(StandardCharsets.UTF_8));
            output.write(content);
        }
    }
}
