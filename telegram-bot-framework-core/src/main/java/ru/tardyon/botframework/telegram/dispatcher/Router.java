package ru.tardyon.botframework.telegram.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.dispatcher.filter.ContextFilter;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filter;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.dispatcher.handler.CallbackQueryHandler;
import ru.tardyon.botframework.telegram.dispatcher.handler.ChosenInlineResultHandler;
import ru.tardyon.botframework.telegram.dispatcher.handler.InlineQueryHandler;
import ru.tardyon.botframework.telegram.dispatcher.handler.MessageHandler;
import ru.tardyon.botframework.telegram.dispatcher.handler.PreCheckoutQueryHandler;
import ru.tardyon.botframework.telegram.dispatcher.handler.ShippingQueryHandler;

public class Router {

    private final List<MessageRoute> messageRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextMessageRoute> contextMessageRoutes = new CopyOnWriteArrayList<>();
    private final List<CallbackRoute> callbackRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextCallbackRoute> contextCallbackRoutes = new CopyOnWriteArrayList<>();
    private final List<InlineQueryRoute> inlineQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextInlineQueryRoute> contextInlineQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<ChosenInlineResultRoute> chosenInlineResultRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextChosenInlineResultRoute> contextChosenInlineResultRoutes = new CopyOnWriteArrayList<>();
    private final List<ShippingQueryRoute> shippingQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextShippingQueryRoute> contextShippingQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<PreCheckoutQueryRoute> preCheckoutQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<ContextPreCheckoutQueryRoute> contextPreCheckoutQueryRoutes = new CopyOnWriteArrayList<>();
    private final List<Router> includedRouters = new CopyOnWriteArrayList<>();

    public Router message(Filter<Message> filter, MessageHandler handler) {
        messageRoutes.add(new MessageRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router message(MessageHandler handler) {
        return message(Filters.any(), handler);
    }

    public Router message(ContextFilter<Message> filter, MessageHandler handler) {
        contextMessageRoutes.add(new ContextMessageRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router callbackQuery(Filter<CallbackQuery> filter, CallbackQueryHandler handler) {
        callbackRoutes.add(new CallbackRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router callbackQuery(CallbackQueryHandler handler) {
        return callbackQuery(Filters.any(), handler);
    }

    public Router callbackQuery(ContextFilter<CallbackQuery> filter, CallbackQueryHandler handler) {
        contextCallbackRoutes.add(new ContextCallbackRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router inlineQuery(Filter<InlineQuery> filter, InlineQueryHandler handler) {
        inlineQueryRoutes.add(new InlineQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router inlineQuery(InlineQueryHandler handler) {
        return inlineQuery(Filters.any(), handler);
    }

    public Router inlineQuery(ContextFilter<InlineQuery> filter, InlineQueryHandler handler) {
        contextInlineQueryRoutes.add(new ContextInlineQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router chosenInlineResult(Filter<ChosenInlineResult> filter, ChosenInlineResultHandler handler) {
        chosenInlineResultRoutes.add(new ChosenInlineResultRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router chosenInlineResult(ChosenInlineResultHandler handler) {
        return chosenInlineResult(Filters.any(), handler);
    }

    public Router chosenInlineResult(ContextFilter<ChosenInlineResult> filter, ChosenInlineResultHandler handler) {
        contextChosenInlineResultRoutes.add(new ContextChosenInlineResultRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router shippingQuery(Filter<ShippingQuery> filter, ShippingQueryHandler handler) {
        shippingQueryRoutes.add(new ShippingQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router shippingQuery(ShippingQueryHandler handler) {
        return shippingQuery(Filters.any(), handler);
    }

    public Router shippingQuery(ContextFilter<ShippingQuery> filter, ShippingQueryHandler handler) {
        contextShippingQueryRoutes.add(new ContextShippingQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router preCheckoutQuery(Filter<PreCheckoutQuery> filter, PreCheckoutQueryHandler handler) {
        preCheckoutQueryRoutes.add(new PreCheckoutQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router preCheckoutQuery(PreCheckoutQueryHandler handler) {
        return preCheckoutQuery(Filters.any(), handler);
    }

    public Router preCheckoutQuery(ContextFilter<PreCheckoutQuery> filter, PreCheckoutQueryHandler handler) {
        contextPreCheckoutQueryRoutes.add(new ContextPreCheckoutQueryRoute(
            Objects.requireNonNull(filter, "filter must not be null"),
            Objects.requireNonNull(handler, "handler must not be null")
        ));
        return this;
    }

    public Router include(Router router) {
        includedRouters.add(Objects.requireNonNull(router, "router must not be null"));
        return this;
    }

    public void route(UpdateContext updateContext) {
        Objects.requireNonNull(updateContext, "updateContext must not be null");

        Message message = updateContext.getMessage();
        if (message != null) {
            routeMessage(updateContext, message);
        }

        CallbackQuery callbackQuery = updateContext.getCallbackQuery();
        if (callbackQuery != null) {
            routeCallback(updateContext, callbackQuery);
        }
        ShippingQuery shippingQuery = updateContext.getShippingQuery();
        if (shippingQuery != null) {
            routeShippingQuery(updateContext, shippingQuery);
        }
        PreCheckoutQuery preCheckoutQuery = updateContext.getPreCheckoutQuery();
        if (preCheckoutQuery != null) {
            routePreCheckoutQuery(updateContext, preCheckoutQuery);
        }
        InlineQuery inlineQuery = updateContext.getInlineQuery();
        if (inlineQuery != null) {
            routeInlineQuery(updateContext, inlineQuery);
        }
        ChosenInlineResult chosenInlineResult = updateContext.getChosenInlineResult();
        if (chosenInlineResult != null) {
            routeChosenInlineResult(updateContext, chosenInlineResult);
        }

        for (Router router : includedRouters) {
            router.route(updateContext);
        }
    }

    private void routeMessage(UpdateContext context, Message message) {
        List<MessageHandler> matchedHandlers = new ArrayList<>();
        for (MessageRoute route : messageRoutes) {
            if (route.filter().test(message)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextMessageRoute route : contextMessageRoutes) {
            if (route.filter().test(context, message)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (MessageHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, message);
        }
    }

    private void routeCallback(UpdateContext context, CallbackQuery callbackQuery) {
        List<CallbackQueryHandler> matchedHandlers = new ArrayList<>();
        for (CallbackRoute route : callbackRoutes) {
            if (route.filter().test(callbackQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextCallbackRoute route : contextCallbackRoutes) {
            if (route.filter().test(context, callbackQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (CallbackQueryHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, callbackQuery);
        }
    }

    private void routeInlineQuery(UpdateContext context, InlineQuery inlineQuery) {
        List<InlineQueryHandler> matchedHandlers = new ArrayList<>();
        for (InlineQueryRoute route : inlineQueryRoutes) {
            if (route.filter().test(inlineQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextInlineQueryRoute route : contextInlineQueryRoutes) {
            if (route.filter().test(context, inlineQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (InlineQueryHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, inlineQuery);
        }
    }

    private void routeChosenInlineResult(UpdateContext context, ChosenInlineResult chosenInlineResult) {
        List<ChosenInlineResultHandler> matchedHandlers = new ArrayList<>();
        for (ChosenInlineResultRoute route : chosenInlineResultRoutes) {
            if (route.filter().test(chosenInlineResult)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextChosenInlineResultRoute route : contextChosenInlineResultRoutes) {
            if (route.filter().test(context, chosenInlineResult)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ChosenInlineResultHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, chosenInlineResult);
        }
    }

    private void routeShippingQuery(UpdateContext context, ShippingQuery shippingQuery) {
        List<ShippingQueryHandler> matchedHandlers = new ArrayList<>();
        for (ShippingQueryRoute route : shippingQueryRoutes) {
            if (route.filter().test(shippingQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextShippingQueryRoute route : contextShippingQueryRoutes) {
            if (route.filter().test(context, shippingQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ShippingQueryHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, shippingQuery);
        }
    }

    private void routePreCheckoutQuery(UpdateContext context, PreCheckoutQuery preCheckoutQuery) {
        List<PreCheckoutQueryHandler> matchedHandlers = new ArrayList<>();
        for (PreCheckoutQueryRoute route : preCheckoutQueryRoutes) {
            if (route.filter().test(preCheckoutQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (ContextPreCheckoutQueryRoute route : contextPreCheckoutQueryRoutes) {
            if (route.filter().test(context, preCheckoutQuery)) {
                matchedHandlers.add(route.handler());
            }
        }
        for (PreCheckoutQueryHandler matchedHandler : matchedHandlers) {
            matchedHandler.handle(context, preCheckoutQuery);
        }
    }

    private record MessageRoute(Filter<Message> filter, MessageHandler handler) {
    }

    private record CallbackRoute(Filter<CallbackQuery> filter, CallbackQueryHandler handler) {
    }

    private record ContextMessageRoute(ContextFilter<Message> filter, MessageHandler handler) {
    }

    private record ContextCallbackRoute(ContextFilter<CallbackQuery> filter, CallbackQueryHandler handler) {
    }

    private record InlineQueryRoute(Filter<InlineQuery> filter, InlineQueryHandler handler) {
    }

    private record ContextInlineQueryRoute(ContextFilter<InlineQuery> filter, InlineQueryHandler handler) {
    }

    private record ChosenInlineResultRoute(Filter<ChosenInlineResult> filter, ChosenInlineResultHandler handler) {
    }

    private record ContextChosenInlineResultRoute(
        ContextFilter<ChosenInlineResult> filter,
        ChosenInlineResultHandler handler
    ) {
    }

    private record ShippingQueryRoute(Filter<ShippingQuery> filter, ShippingQueryHandler handler) {
    }

    private record ContextShippingQueryRoute(ContextFilter<ShippingQuery> filter, ShippingQueryHandler handler) {
    }

    private record PreCheckoutQueryRoute(Filter<PreCheckoutQuery> filter, PreCheckoutQueryHandler handler) {
    }

    private record ContextPreCheckoutQueryRoute(
        ContextFilter<PreCheckoutQuery> filter,
        PreCheckoutQueryHandler handler
    ) {
    }
}
