package ru.tardyon.botframework.telegram.spring.boot.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.ReflectionUtils;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppData;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filter;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

public final class TelegramAnnotationHandlerRegistrar implements SmartInitializingSingleton {

    private final Router router;
    private final ListableBeanFactory beanFactory;

    public TelegramAnnotationHandlerRegistrar(Router router, ListableBeanFactory beanFactory) {
        this.router = Objects.requireNonNull(router, "router must not be null");
        this.beanFactory = Objects.requireNonNull(beanFactory, "beanFactory must not be null");
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(BotController.class);
        for (Object controller : controllers.values()) {
            registerController(controller);
        }
    }

    private void registerController(Object controllerBean) {
        Class<?> targetClass = AopUtils.getTargetClass(controllerBean);
        ReflectionUtils.doWithMethods(targetClass, method -> registerMethod(controllerBean, method), this::isAnnotatedHandlerMethod);
    }

    private boolean isAnnotatedHandlerMethod(Method method) {
        return method.isAnnotationPresent(OnMessage.class)
            || method.isAnnotationPresent(OnCallbackQuery.class)
            || method.isAnnotationPresent(OnInlineQuery.class)
            || method.isAnnotationPresent(OnChosenInlineResult.class)
            || method.isAnnotationPresent(OnShippingQuery.class)
            || method.isAnnotationPresent(OnPreCheckoutQuery.class)
            || method.isAnnotationPresent(OnBusinessConnection.class)
            || method.isAnnotationPresent(OnBusinessMessage.class)
            || method.isAnnotationPresent(OnEditedBusinessMessage.class)
            || method.isAnnotationPresent(OnDeletedBusinessMessages.class);
    }

    private void registerMethod(Object bean, Method method) {
        validateMethodCommon(method);
        if (method.isAnnotationPresent(OnMessage.class)) {
            registerMessageHandler(bean, method, method.getAnnotation(OnMessage.class));
        }
        if (method.isAnnotationPresent(OnCallbackQuery.class)) {
            registerCallbackHandler(bean, method, method.getAnnotation(OnCallbackQuery.class));
        }
        if (method.isAnnotationPresent(OnInlineQuery.class)) {
            registerInlineQueryHandler(bean, method, method.getAnnotation(OnInlineQuery.class));
        }
        if (method.isAnnotationPresent(OnChosenInlineResult.class)) {
            registerChosenInlineResultHandler(bean, method, method.getAnnotation(OnChosenInlineResult.class));
        }
        if (method.isAnnotationPresent(OnShippingQuery.class)) {
            registerShippingQueryHandler(bean, method, method.getAnnotation(OnShippingQuery.class));
        }
        if (method.isAnnotationPresent(OnPreCheckoutQuery.class)) {
            registerPreCheckoutQueryHandler(bean, method, method.getAnnotation(OnPreCheckoutQuery.class));
        }
        if (method.isAnnotationPresent(OnBusinessConnection.class)) {
            registerBusinessConnectionHandler(bean, method, method.getAnnotation(OnBusinessConnection.class));
        }
        if (method.isAnnotationPresent(OnBusinessMessage.class)) {
            registerBusinessMessageHandler(bean, method, method.getAnnotation(OnBusinessMessage.class));
        }
        if (method.isAnnotationPresent(OnEditedBusinessMessage.class)) {
            registerEditedBusinessMessageHandler(bean, method, method.getAnnotation(OnEditedBusinessMessage.class));
        }
        if (method.isAnnotationPresent(OnDeletedBusinessMessages.class)) {
            registerDeletedBusinessMessagesHandler(bean, method, method.getAnnotation(OnDeletedBusinessMessages.class));
        }
    }

    private void registerMessageHandler(Object bean, Method method, OnMessage annotation) {
        validateParameters(method, HandlerType.MESSAGE);
        Filter<Message> messageFilter = buildMessageFilter(annotation);
        String state = annotation.state();

        router.message((context, message) -> {
            if (!messageFilter.test(message)) {
                return false;
            }
            return !hasText(state) || Filters.<Message>stateEquals(state).test(context, message);
        }, (context, message) -> invokeHandler(bean, method, HandlerType.MESSAGE, context, message));
    }

    private Filter<Message> buildMessageFilter(OnMessage annotation) {
        Filter<Message> filter = Filters.any();
        if (hasText(annotation.command())) {
            filter = filter.and(Filters.command(annotation.command()));
        }
        if (hasText(annotation.textEquals())) {
            filter = filter.and(Filters.textEquals(annotation.textEquals()));
        }
        if (hasText(annotation.textStartsWith())) {
            filter = filter.and(Filters.textStartsWith(annotation.textStartsWith()));
        }
        if (annotation.webAppDataPresent()) {
            filter = filter.and(message -> message != null && message.webAppData() != null);
        }
        return filter;
    }

    private void registerCallbackHandler(Object bean, Method method, OnCallbackQuery annotation) {
        validateParameters(method, HandlerType.CALLBACK_QUERY);
        String callbackPrefix = annotation.callbackPrefix();
        String state = annotation.state();

        router.callbackQuery((context, callbackQuery) -> {
            boolean callbackMatches = !hasText(callbackPrefix)
                || Filters.callbackDataStartsWith(callbackPrefix).test(callbackQuery);
            if (!callbackMatches) {
                return false;
            }
            return !hasText(state) || Filters.<CallbackQuery>stateEquals(state).test(context, callbackQuery);
        }, (context, callbackQuery) -> invokeHandler(bean, method, HandlerType.CALLBACK_QUERY, context, callbackQuery));
    }

    private void registerInlineQueryHandler(Object bean, Method method, OnInlineQuery annotation) {
        validateParameters(method, HandlerType.INLINE_QUERY);
        String state = annotation.state();
        router.inlineQuery((context, inlineQuery) -> !hasText(state) || Filters.<InlineQuery>stateEquals(state).test(context, inlineQuery),
            (context, inlineQuery) -> invokeHandler(bean, method, HandlerType.INLINE_QUERY, context, inlineQuery)
        );
    }

    private void registerChosenInlineResultHandler(Object bean, Method method, OnChosenInlineResult annotation) {
        validateParameters(method, HandlerType.CHOSEN_INLINE_RESULT);
        String state = annotation.state();
        router.chosenInlineResult(
            (context, chosenInlineResult) -> !hasText(state)
                || Filters.<ChosenInlineResult>stateEquals(state).test(context, chosenInlineResult),
            (context, chosenInlineResult) -> invokeHandler(bean, method, HandlerType.CHOSEN_INLINE_RESULT, context, chosenInlineResult)
        );
    }

    private void registerShippingQueryHandler(Object bean, Method method, OnShippingQuery annotation) {
        validateParameters(method, HandlerType.SHIPPING_QUERY);
        String payload = annotation.invoicePayloadEquals();
        String state = annotation.state();
        router.shippingQuery((context, shippingQuery) -> {
            boolean payloadMatches = !hasText(payload) || Filters.invoicePayloadEquals(payload).test(shippingQuery);
            if (!payloadMatches) {
                return false;
            }
            return !hasText(state) || Filters.<ShippingQuery>stateEquals(state).test(context, shippingQuery);
        }, (context, shippingQuery) -> invokeHandler(bean, method, HandlerType.SHIPPING_QUERY, context, shippingQuery));
    }

    private void registerPreCheckoutQueryHandler(Object bean, Method method, OnPreCheckoutQuery annotation) {
        validateParameters(method, HandlerType.PRE_CHECKOUT_QUERY);
        String payload = annotation.payloadEquals();
        String state = annotation.state();
        router.preCheckoutQuery((context, preCheckoutQuery) -> {
            boolean payloadMatches = !hasText(payload) || Filters.preCheckoutPayloadEquals(payload).test(preCheckoutQuery);
            if (!payloadMatches) {
                return false;
            }
            return !hasText(state) || Filters.<PreCheckoutQuery>stateEquals(state).test(context, preCheckoutQuery);
        }, (context, preCheckoutQuery) -> invokeHandler(bean, method, HandlerType.PRE_CHECKOUT_QUERY, context, preCheckoutQuery));
    }

    private void registerBusinessConnectionHandler(Object bean, Method method, OnBusinessConnection annotation) {
        validateParameters(method, HandlerType.BUSINESS_CONNECTION);
        String state = annotation.state();
        router.businessConnection((context, businessConnection) -> !hasText(state)
                || Filters.<BusinessConnection>stateEquals(state).test(context, businessConnection),
            (context, businessConnection) -> invokeHandler(bean, method, HandlerType.BUSINESS_CONNECTION, context, businessConnection)
        );
    }

    private void registerBusinessMessageHandler(Object bean, Method method, OnBusinessMessage annotation) {
        validateParameters(method, HandlerType.BUSINESS_MESSAGE);
        Filter<Message> messageFilter = buildBusinessMessageFilter(annotation.textEquals(), annotation.textStartsWith());
        String state = annotation.state();
        router.businessMessage((context, message) -> {
            if (!messageFilter.test(message)) {
                return false;
            }
            return !hasText(state) || Filters.<Message>stateEquals(state).test(context, message);
        }, (context, message) -> invokeHandler(bean, method, HandlerType.BUSINESS_MESSAGE, context, message));
    }

    private void registerEditedBusinessMessageHandler(Object bean, Method method, OnEditedBusinessMessage annotation) {
        validateParameters(method, HandlerType.EDITED_BUSINESS_MESSAGE);
        Filter<Message> messageFilter = buildBusinessMessageFilter(annotation.textEquals(), annotation.textStartsWith());
        String state = annotation.state();
        router.editedBusinessMessage((context, message) -> {
            if (!messageFilter.test(message)) {
                return false;
            }
            return !hasText(state) || Filters.<Message>stateEquals(state).test(context, message);
        }, (context, message) -> invokeHandler(bean, method, HandlerType.EDITED_BUSINESS_MESSAGE, context, message));
    }

    private void registerDeletedBusinessMessagesHandler(Object bean, Method method, OnDeletedBusinessMessages annotation) {
        validateParameters(method, HandlerType.DELETED_BUSINESS_MESSAGES);
        String state = annotation.state();
        router.deletedBusinessMessages((context, deleted) -> !hasText(state)
                || Filters.<BusinessMessagesDeleted>stateEquals(state).test(context, deleted),
            (context, deleted) -> invokeHandler(bean, method, HandlerType.DELETED_BUSINESS_MESSAGES, context, deleted)
        );
    }

    private Filter<Message> buildBusinessMessageFilter(String textEquals, String textStartsWith) {
        Filter<Message> filter = Filters.any();
        if (hasText(textEquals)) {
            filter = filter.and(Filters.textEquals(textEquals));
        }
        if (hasText(textStartsWith)) {
            filter = filter.and(Filters.textStartsWith(textStartsWith));
        }
        return filter;
    }

    private void invokeHandler(Object bean, Method method, HandlerType handlerType, UpdateContext context, Object event) {
        Object[] arguments = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveParameter(parameters[i].getType(), handlerType, context, event);
        }

        try {
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean, arguments);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException() == null ? e : e.getTargetException();
            throw new IllegalStateException("Bot handler failed: " + method, targetException);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access bot handler: " + method, e);
        }
    }

    private void validateMethodCommon(Method method) {
        if (method.getReturnType() != Void.TYPE) {
            throw new IllegalStateException("Bot handler method must return void: " + method);
        }
    }

    private void validateParameters(Method method, HandlerType handlerType) {
        for (Parameter parameter : method.getParameters()) {
            if (!isSupportedParameter(handlerType, parameter.getType())) {
                throw new IllegalStateException(
                    "Unsupported parameter type for " + handlerType + " handler method " + method + ": " + parameter.getType().getName()
                );
            }
        }
    }

    private boolean isSupportedParameter(HandlerType handlerType, Class<?> parameterType) {
        if (parameterType == UpdateContext.class || parameterType == Update.class) {
            return true;
        }
        return switch (handlerType) {
            case MESSAGE, BUSINESS_MESSAGE, EDITED_BUSINESS_MESSAGE ->
                parameterType == Message.class || (handlerType == HandlerType.MESSAGE && parameterType == TelegramMessage.class)
                    || parameterType == WebAppData.class;
            case CALLBACK_QUERY -> parameterType == CallbackQuery.class || parameterType == TelegramCallbackQuery.class;
            case INLINE_QUERY -> parameterType == InlineQuery.class;
            case CHOSEN_INLINE_RESULT -> parameterType == ChosenInlineResult.class;
            case SHIPPING_QUERY -> parameterType == ShippingQuery.class;
            case PRE_CHECKOUT_QUERY -> parameterType == PreCheckoutQuery.class;
            case BUSINESS_CONNECTION -> parameterType == BusinessConnection.class;
            case DELETED_BUSINESS_MESSAGES -> parameterType == BusinessMessagesDeleted.class;
        };
    }

    private Object resolveParameter(Class<?> parameterType, HandlerType handlerType, UpdateContext context, Object event) {
        if (parameterType == UpdateContext.class) {
            return context;
        }
        if (parameterType == Update.class) {
            return context.getUpdate();
        }

        return switch (handlerType) {
            case MESSAGE -> resolveMessageParameter(parameterType, context, event);
            case BUSINESS_MESSAGE, EDITED_BUSINESS_MESSAGE -> resolveBusinessMessageParameter(parameterType, context, event);
            case CALLBACK_QUERY -> resolveCallbackParameter(parameterType, context, event);
            case SHIPPING_QUERY, PRE_CHECKOUT_QUERY, BUSINESS_CONNECTION, DELETED_BUSINESS_MESSAGES, INLINE_QUERY, CHOSEN_INLINE_RESULT -> event;
        };
    }

    private Object resolveMessageParameter(Class<?> parameterType, UpdateContext context, Object event) {
        if (parameterType == Message.class) {
            return event;
        }
        if (parameterType == WebAppData.class) {
            return ((Message) event).webAppData();
        }
        if (parameterType == TelegramMessage.class) {
            return context.telegramMessage();
        }
        throw new IllegalStateException("Unsupported message parameter type: " + parameterType.getName());
    }

    private Object resolveCallbackParameter(Class<?> parameterType, UpdateContext context, Object event) {
        if (parameterType == CallbackQuery.class) {
            return event;
        }
        if (parameterType == TelegramCallbackQuery.class) {
            return context.telegramCallbackQuery();
        }
        throw new IllegalStateException("Unsupported callback parameter type: " + parameterType.getName());
    }

    private Object resolveBusinessMessageParameter(Class<?> parameterType, UpdateContext context, Object event) {
        if (parameterType == Message.class) {
            return event;
        }
        if (parameterType == WebAppData.class) {
            return ((Message) event).webAppData();
        }
        throw new IllegalStateException("Unsupported business message parameter type: " + parameterType.getName());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private enum HandlerType {
        MESSAGE,
        SHIPPING_QUERY,
        PRE_CHECKOUT_QUERY,
        BUSINESS_CONNECTION,
        BUSINESS_MESSAGE,
        EDITED_BUSINESS_MESSAGE,
        DELETED_BUSINESS_MESSAGES,
        CALLBACK_QUERY,
        INLINE_QUERY,
        CHOSEN_INLINE_RESULT
    }
}
