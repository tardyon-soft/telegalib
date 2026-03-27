package ru.tardyon.botframework.telegram.diagnostics;

public interface UpdateProcessingListener {

    default void onUpdateStarted(UpdateProcessingStartedEvent event) {
    }

    default void onUpdateFinished(UpdateProcessingFinishedEvent event) {
    }
}
