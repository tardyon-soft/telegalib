package ru.tardyon.botframework.telegram.dispatcher;

public interface Dispatcher {

    void dispatch(UpdateContext updateContext);
}
