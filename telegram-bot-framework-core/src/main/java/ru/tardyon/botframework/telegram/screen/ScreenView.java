package ru.tardyon.botframework.telegram.screen;

import java.util.Collection;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record ScreenView(
    String text,
    ReplyMarkup replyMarkup,
    ScreenRenderMode renderMode,
    InputFile photo
) {
    public ScreenView {
        Objects.requireNonNull(text, "text must not be null");
        renderMode = renderMode == null ? ScreenRenderMode.AUTO : renderMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final StringBuilder textBuilder = new StringBuilder();
        private ReplyMarkup replyMarkup;
        private ScreenRenderMode renderMode = ScreenRenderMode.AUTO;
        private InputFile photo;

        public Builder text(String text) {
            textBuilder.setLength(0);
            textBuilder.append(Objects.requireNonNull(text, "text must not be null"));
            return this;
        }

        public Builder append(String textPart) {
            textBuilder.append(Objects.requireNonNull(textPart, "textPart must not be null"));
            return this;
        }

        public Builder line(String line) {
            Objects.requireNonNull(line, "line must not be null");
            if (!textBuilder.isEmpty()) {
                textBuilder.append('\n');
            }
            textBuilder.append(line);
            return this;
        }

        public Builder replyMarkup(ReplyMarkup replyMarkup) {
            this.replyMarkup = replyMarkup;
            return this;
        }

        public Builder renderMode(ScreenRenderMode renderMode) {
            this.renderMode = Objects.requireNonNull(renderMode, "renderMode must not be null");
            return this;
        }

        public Builder photo(InputFile photo) {
            this.photo = Objects.requireNonNull(photo, "photo must not be null");
            return this;
        }

        public Builder photoUrl(String url) {
            this.photo = InputFile.url(url);
            return this;
        }

        public Builder widget(Widget widget, ScreenContext context) {
            Objects.requireNonNull(widget, "widget must not be null");
            widget.apply(this, context);
            return this;
        }

        public Builder widgets(Collection<? extends Widget> widgets, ScreenContext context) {
            Objects.requireNonNull(widgets, "widgets must not be null");
            for (Widget widget : widgets) {
                widget(widget, context);
            }
            return this;
        }

        public ScreenView build() {
            return new ScreenView(textBuilder.toString(), replyMarkup, renderMode, photo);
        }
    }
}
