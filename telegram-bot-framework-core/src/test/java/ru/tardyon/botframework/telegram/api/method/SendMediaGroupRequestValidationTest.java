package ru.tardyon.botframework.telegram.api.method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaAudio;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaDocument;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaVideo;

class SendMediaGroupRequestValidationTest {

    @Test
    void rejectsMediaCountOutsideRange() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SendMediaGroupRequest.of(1L, List.of(new InputMediaPhoto(InputFile.fileId("p1"))))
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaPhoto(InputFile.fileId("1")),
                    new InputMediaPhoto(InputFile.fileId("2")),
                    new InputMediaPhoto(InputFile.fileId("3")),
                    new InputMediaPhoto(InputFile.fileId("4")),
                    new InputMediaPhoto(InputFile.fileId("5")),
                    new InputMediaPhoto(InputFile.fileId("6")),
                    new InputMediaPhoto(InputFile.fileId("7")),
                    new InputMediaPhoto(InputFile.fileId("8")),
                    new InputMediaPhoto(InputFile.fileId("9")),
                    new InputMediaPhoto(InputFile.fileId("10")),
                    new InputMediaPhoto(InputFile.fileId("11"))
                )
            )
        );
    }

    @Test
    void rejectsMixedDocumentAlbum() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaDocument(InputFile.fileId("doc-1")),
                    new InputMediaPhoto(InputFile.fileId("photo-1"))
                )
            )
        );
    }

    @Test
    void rejectsMixedAudioAlbum() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaAudio(InputFile.fileId("aud-1")),
                    new InputMediaVideo(InputFile.fileId("vid-1"))
                )
            )
        );
    }

    @Test
    void allowsPhotoVideoMixAndHomogeneousAudioDocumentAlbums() {
        assertDoesNotThrow(
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaPhoto(InputFile.fileId("p-1")),
                    new InputMediaVideo(InputFile.fileId("v-1"))
                )
            )
        );
        assertDoesNotThrow(
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaAudio(InputFile.fileId("a-1")),
                    new InputMediaAudio(InputFile.fileId("a-2"))
                )
            )
        );
        assertDoesNotThrow(
            () -> SendMediaGroupRequest.of(
                1L,
                List.of(
                    new InputMediaDocument(InputFile.fileId("d-1")),
                    new InputMediaDocument(InputFile.fileId("d-2"))
                )
            )
        );
    }
}
