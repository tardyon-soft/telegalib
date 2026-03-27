package ru.tardyon.botframework.telegram.spring.boot.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filter;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.fsm.StateContext;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenEngine;
import ru.tardyon.botframework.telegram.screen.ScreenNavigator;
import ru.tardyon.botframework.telegram.screen.ScreenRegistry;
import ru.tardyon.botframework.telegram.screen.ScreenStateContext;
import ru.tardyon.botframework.telegram.screen.ScreenView;

public final class TelegramScreenAnnotationRegistrar implements SmartInitializingSingleton {

    private final Router router;
    private final ScreenRegistry screenRegistry;
    private final ScreenEngine screenEngine;
    private final ListableBeanFactory beanFactory;

    public TelegramScreenAnnotationRegistrar(
        Router router,
        ScreenRegistry screenRegistry,
        ScreenEngine screenEngine,
        ListableBeanFactory beanFactory
    ) {
        this.router = Objects.requireNonNull(router, "router must not be null");
        this.screenRegistry = Objects.requireNonNull(screenRegistry, "screenRegistry must not be null");
        this.screenEngine = Objects.requireNonNull(screenEngine, "screenEngine must not be null");
        this.beanFactory = Objects.requireNonNull(beanFactory, "beanFactory must not be null");
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(ScreenController.class);
        Map<String, ScreenDefinition> definitions = new HashMap<>();

        for (Object controller : controllers.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(controller);
            ReflectionUtils.doWithMethods(targetClass, method -> collectDefinitions(definitions, controller, method));
        }

        for (ScreenDefinition definition : definitions.values()) {
            screenRegistry.register(definition.toScreen());
            if (StringUtils.hasText(definition.startCommand())) {
                String command = definition.startCommand();
                router.message(message -> Filters.command(command).test(message) || Filters.textEquals(command).test(message),
                    (context, message) -> screenEngine.start(context, definition.id())
                );
            }
        }
    }

    private void collectDefinitions(Map<String, ScreenDefinition> definitions, Object bean, Method method) {
        Screen screenAnnotation = method.getAnnotation(Screen.class);
        OnScreenMessage onScreenMessage = method.getAnnotation(OnScreenMessage.class);
        OnScreenCallback onScreenCallback = method.getAnnotation(OnScreenCallback.class);

        if (screenAnnotation == null && onScreenMessage == null && onScreenCallback == null) {
            return;
        }

        if (screenAnnotation != null) {
            validateScreenViewMethod(method);
            String screenId = requireScreenId(screenAnnotation.id(), method);
            ScreenDefinition definition = definitions.computeIfAbsent(screenId, ScreenDefinition::new);
            definition.setRenderMethod(new MethodBinding(bean, method));
            if (StringUtils.hasText(screenAnnotation.startCommand())) {
                definition.setStartCommand(screenAnnotation.startCommand());
            }
        }

        if (onScreenMessage != null) {
            validateScreenHandlerMethod(method, true);
            String screenId = requireScreenId(onScreenMessage.screen(), method);
            ScreenDefinition definition = definitions.computeIfAbsent(screenId, ScreenDefinition::new);
            definition.messageHandlers().add(new MessageHandlerBinding(
                new MethodBinding(bean, method),
                buildMessageFilter(onScreenMessage),
                onScreenMessage.state()
            ));
        }

        if (onScreenCallback != null) {
            validateScreenHandlerMethod(method, false);
            String screenId = requireScreenId(onScreenCallback.screen(), method);
            ScreenDefinition definition = definitions.computeIfAbsent(screenId, ScreenDefinition::new);
            definition.callbackHandlers().add(new CallbackHandlerBinding(
                new MethodBinding(bean, method),
                buildCallbackFilter(onScreenCallback),
                onScreenCallback.state()
            ));
        }
    }

    private Filter<Message> buildMessageFilter(OnScreenMessage annotation) {
        Filter<Message> filter = Filters.any();
        if (StringUtils.hasText(annotation.command())) {
            filter = filter.and(Filters.command(annotation.command()));
        }
        if (StringUtils.hasText(annotation.textEquals())) {
            filter = filter.and(Filters.textEquals(annotation.textEquals()));
        }
        if (StringUtils.hasText(annotation.textStartsWith())) {
            filter = filter.and(Filters.textStartsWith(annotation.textStartsWith()));
        }
        return filter;
    }

    private Filter<CallbackQuery> buildCallbackFilter(OnScreenCallback annotation) {
        Filter<CallbackQuery> filter = Filters.any();
        if (StringUtils.hasText(annotation.callbackPrefix())) {
            filter = filter.and(Filters.callbackDataStartsWith(annotation.callbackPrefix()));
        }
        if (StringUtils.hasText(annotation.callbackEquals())) {
            filter = filter.and(Filters.callbackDataEquals(annotation.callbackEquals()));
        }
        return filter;
    }

    private void validateScreenViewMethod(Method method) {
        if (method.getReturnType() != ScreenView.class) {
            throw new IllegalStateException("Screen view method must return ScreenView: " + method);
        }
        validateParameters(method, true, true);
    }

    private void validateScreenHandlerMethod(Method method, boolean messageHandler) {
        if (method.getReturnType() != Void.TYPE && method.getReturnType() != ScreenAction.class) {
            throw new IllegalStateException("Screen handler must return void or ScreenAction: " + method);
        }
        validateParameters(method, false, messageHandler);
    }

    private void validateParameters(Method method, boolean renderMethod, boolean messageHandler) {
        for (Parameter parameter : method.getParameters()) {
            if (!isSupportedParameter(parameter.getType(), renderMethod, messageHandler)) {
                throw new IllegalStateException("Unsupported parameter type for screen method " + method + ": " + parameter.getType().getName());
            }
        }
    }

    private boolean isSupportedParameter(Class<?> parameterType, boolean renderMethod, boolean messageHandler) {
        if (parameterType == ScreenContext.class
            || parameterType == UpdateContext.class
            || parameterType == ScreenStateContext.class
            || parameterType == ScreenNavigator.class
            || parameterType == StateContext.class) {
            return true;
        }
        if (messageHandler) {
            return parameterType == Message.class || parameterType == TelegramMessage.class;
        }
        if (renderMethod) {
            return parameterType == Message.class || parameterType == TelegramMessage.class;
        }
        return parameterType == CallbackQuery.class || parameterType == TelegramCallbackQuery.class;
    }

    private String requireScreenId(String screenId, Method method) {
        if (!StringUtils.hasText(screenId)) {
            throw new IllegalStateException("Screen id must not be blank for method: " + method);
        }
        return screenId;
    }

    private final class ScreenDefinition {
        private final String id;
        private MethodBinding renderMethod;
        private String startCommand;
        private final List<MessageHandlerBinding> messageHandlers = new ArrayList<>();
        private final List<CallbackHandlerBinding> callbackHandlers = new ArrayList<>();

        private ScreenDefinition(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public String startCommand() {
            return startCommand;
        }

        public void setStartCommand(String startCommand) {
            this.startCommand = startCommand;
        }

        public void setRenderMethod(MethodBinding renderMethod) {
            if (this.renderMethod != null) {
                throw new IllegalStateException("Screen view method already defined for screen: " + id);
            }
            this.renderMethod = Objects.requireNonNull(renderMethod, "renderMethod must not be null");
        }

        public List<MessageHandlerBinding> messageHandlers() {
            return messageHandlers;
        }

        public List<CallbackHandlerBinding> callbackHandlers() {
            return callbackHandlers;
        }

        public ru.tardyon.botframework.telegram.screen.Screen toScreen() {
            if (renderMethod == null) {
                throw new IllegalStateException("Screen view method is missing for screen: " + id);
            }
            return new ru.tardyon.botframework.telegram.screen.Screen() {
                @Override
                public String id() {
                    return ScreenDefinition.this.id;
                }

                @Override
                public ScreenView render(ScreenContext context) {
                    Object result = invoke(renderMethod, context, context.updateContext(), context.message(), null);
                    return (ScreenView) result;
                }

                @Override
                public ScreenAction onMessage(ScreenContext context, Message message) {
                    for (MessageHandlerBinding handler : messageHandlers) {
                        if (!handler.filter().test(message)) {
                            continue;
                        }
                        if (StringUtils.hasText(handler.state()) && !Filters.<Message>stateEquals(handler.state()).test(context.updateContext(), message)) {
                            continue;
                        }
                        Object result = invoke(handler.binding(), context, context.updateContext(), message, null);
                        ScreenAction action = normalizeAction(result);
                        if (action.kind() != ScreenAction.Kind.UNHANDLED) {
                            return action;
                        }
                    }
                    return ScreenAction.unhandled();
                }

                @Override
                public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
                    for (CallbackHandlerBinding handler : callbackHandlers) {
                        if (!handler.filter().test(callbackQuery)) {
                            continue;
                        }
                        if (StringUtils.hasText(handler.state())
                            && !Filters.<CallbackQuery>stateEquals(handler.state()).test(context.updateContext(), callbackQuery)) {
                            continue;
                        }
                        Object result = invoke(handler.binding(), context, context.updateContext(), null, callbackQuery);
                        ScreenAction action = normalizeAction(result);
                        if (action.kind() != ScreenAction.Kind.UNHANDLED) {
                            return action;
                        }
                    }
                    return ScreenAction.unhandled();
                }
            };
        }
    }

    private Object invoke(
        MethodBinding binding,
        ScreenContext screenContext,
        UpdateContext updateContext,
        Message message,
        CallbackQuery callbackQuery
    ) {
        Method method = binding.method();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            args[i] = resolveParameter(type, screenContext, updateContext, message, callbackQuery);
        }
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(binding.bean(), args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access screen method: " + method, e);
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException() == null ? e : e.getTargetException();
            throw new IllegalStateException("Screen method failed: " + method, target);
        }
    }

    private Object resolveParameter(
        Class<?> parameterType,
        ScreenContext screenContext,
        UpdateContext updateContext,
        Message message,
        CallbackQuery callbackQuery
    ) {
        if (parameterType == ScreenContext.class) {
            return screenContext;
        }
        if (parameterType == UpdateContext.class) {
            return updateContext;
        }
        if (parameterType == ScreenStateContext.class) {
            return screenContext.screenState();
        }
        if (parameterType == ScreenNavigator.class) {
            return screenContext.navigator();
        }
        if (parameterType == StateContext.class) {
            return screenContext.userState();
        }
        if (parameterType == Message.class) {
            return message != null ? message : screenContext.message();
        }
        if (parameterType == CallbackQuery.class) {
            return callbackQuery != null ? callbackQuery : screenContext.callbackQuery();
        }
        if (parameterType == TelegramMessage.class) {
            return screenContext.telegramMessage();
        }
        if (parameterType == TelegramCallbackQuery.class) {
            return screenContext.telegramCallbackQuery();
        }
        throw new IllegalStateException("Unsupported parameter type: " + parameterType.getName());
    }

    private ScreenAction normalizeAction(Object result) {
        if (result == null) {
            return ScreenAction.handled();
        }
        if (result instanceof ScreenAction action) {
            return action;
        }
        throw new IllegalStateException("Screen handler returned unsupported value: " + result.getClass().getName());
    }

    private record MethodBinding(Object bean, Method method) {
    }

    private record MessageHandlerBinding(MethodBinding binding, Filter<Message> filter, String state) {
    }

    private record CallbackHandlerBinding(MethodBinding binding, Filter<CallbackQuery> filter, String state) {
    }
}
