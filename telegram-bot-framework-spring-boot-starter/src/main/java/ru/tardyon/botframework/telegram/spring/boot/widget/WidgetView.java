package ru.tardyon.botframework.telegram.spring.boot.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenView;

public final class WidgetView {

    private final List<String> lines;
    private final ReplyMarkup replyMarkup;
    private final List<WidgetEffect> effects;

    private WidgetView(List<String> lines, ReplyMarkup replyMarkup, List<WidgetEffect> effects) {
        this.lines = List.copyOf(lines);
        this.replyMarkup = replyMarkup;
        this.effects = List.copyOf(effects);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> lines() {
        return lines;
    }

    public ReplyMarkup replyMarkup() {
        return replyMarkup;
    }

    public List<WidgetEffect> effects() {
        return effects;
    }

    public void applyEffects(ScreenContext context) {
        for (WidgetEffect effect : effects) {
            effect.apply(context);
        }
    }

    public ScreenView mergeInto(ScreenView base) {
        Objects.requireNonNull(base, "base must not be null");
        ScreenView.Builder builder = ScreenView.builder().text(base.text());
        for (String line : lines) {
            builder.line(line);
        }
        ReplyMarkup markupToUse = mergeMarkup(base.replyMarkup(), replyMarkup);
        builder.replyMarkup(markupToUse);
        builder.renderMode(base.renderMode());
        return builder.build();
    }

    private ReplyMarkup mergeMarkup(ReplyMarkup base, ReplyMarkup addon) {
        if (addon == null) {
            return base;
        }
        if (base == null) {
            return addon;
        }
        if (base instanceof InlineKeyboardMarkup left && addon instanceof InlineKeyboardMarkup right) {
            List<List<ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardButton>> rows = new ArrayList<>();
            rows.addAll(left.inlineKeyboard());
            rows.addAll(right.inlineKeyboard());
            return new InlineKeyboardMarkup(rows);
        }
        return addon;
    }

    public static final class Builder {
        private final List<String> lines = new ArrayList<>();
        private ReplyMarkup replyMarkup;
        private final List<WidgetEffect> effects = new ArrayList<>();

        public Builder line(String line) {
            lines.add(Objects.requireNonNull(line, "line must not be null"));
            return this;
        }

        public Builder lines(List<String> lines) {
            this.lines.addAll(Objects.requireNonNull(lines, "lines must not be null"));
            return this;
        }

        public Builder replyMarkup(ReplyMarkup replyMarkup) {
            this.replyMarkup = replyMarkup;
            return this;
        }

        public Builder effect(WidgetEffect effect) {
            effects.add(Objects.requireNonNull(effect, "effect must not be null"));
            return this;
        }

        public WidgetView build() {
            return new WidgetView(lines, replyMarkup, effects);
        }
    }
}
