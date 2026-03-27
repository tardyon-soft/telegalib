package ru.tardyon.botframework.telegram.screen;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class ScreenStack {
    public enum RenderedMessageKind {
        TEXT,
        PHOTO
    }

    private final Deque<ScreenFrame> frames = new ArrayDeque<>();
    private Integer renderedMessageId;
    private RenderedMessageKind renderedMessageKind;

    public synchronized void push(String screenId) {
        frames.push(new ScreenFrame(screenId));
    }

    public synchronized void replace(String screenId) {
        if (!frames.isEmpty()) {
            frames.pop();
        }
        frames.push(new ScreenFrame(screenId));
    }

    public synchronized boolean back() {
        if (frames.size() <= 1) {
            return false;
        }
        frames.pop();
        return true;
    }

    public synchronized Optional<ScreenFrame> current() {
        return Optional.ofNullable(frames.peek());
    }

    public synchronized int size() {
        return frames.size();
    }

    public synchronized boolean isEmpty() {
        return frames.isEmpty();
    }

    public synchronized void clear() {
        frames.clear();
        renderedMessageId = null;
        renderedMessageKind = null;
    }

    public synchronized Optional<Integer> renderedMessageId() {
        return Optional.ofNullable(renderedMessageId);
    }

    public synchronized void setRenderedMessageId(Integer renderedMessageId) {
        this.renderedMessageId = renderedMessageId;
    }

    public synchronized Optional<RenderedMessageKind> renderedMessageKind() {
        return Optional.ofNullable(renderedMessageKind);
    }

    public synchronized void setRenderedMessageKind(RenderedMessageKind renderedMessageKind) {
        this.renderedMessageKind = renderedMessageKind;
    }
}
