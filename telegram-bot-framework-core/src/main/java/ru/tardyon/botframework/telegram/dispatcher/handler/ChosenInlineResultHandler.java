package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface ChosenInlineResultHandler {

    void handle(UpdateContext context, ChosenInlineResult chosenInlineResult);
}
