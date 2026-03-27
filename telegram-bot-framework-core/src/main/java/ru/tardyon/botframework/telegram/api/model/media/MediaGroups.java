package ru.tardyon.botframework.telegram.api.model.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public final class MediaGroups {

    private MediaGroups() {
    }

    public static InputMediaPhoto photo(InputFile media) {
        return InputMediaPhoto.of(media);
    }

    public static InputMediaVideo video(InputFile media) {
        return InputMediaVideo.of(media);
    }

    public static InputMediaDocument document(InputFile media) {
        return InputMediaDocument.of(media);
    }

    public static InputMediaAudio audio(InputFile media) {
        return InputMediaAudio.of(media);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<InputMedia> items = new ArrayList<>();

        public Builder add(InputMedia media) {
            items.add(media);
            return this;
        }

        public Builder addAll(InputMedia... media) {
            items.addAll(Arrays.asList(media));
            return this;
        }

        public Builder photo(InputFile media) {
            return add(MediaGroups.photo(media));
        }

        public Builder video(InputFile media) {
            return add(MediaGroups.video(media));
        }

        public Builder document(InputFile media) {
            return add(MediaGroups.document(media));
        }

        public Builder audio(InputFile media) {
            return add(MediaGroups.audio(media));
        }

        public List<InputMedia> build() {
            return List.copyOf(items);
        }
    }
}
