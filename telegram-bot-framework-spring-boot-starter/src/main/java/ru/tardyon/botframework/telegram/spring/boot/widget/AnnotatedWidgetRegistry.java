package ru.tardyon.botframework.telegram.spring.boot.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenContext;

public final class AnnotatedWidgetRegistry {

    private static final String CALLBACK_PREFIX = "w:";

    private final Map<String, WidgetRenderHandler> renderHandlers = new HashMap<>();
    private final Map<String, WidgetActionHandler> actionHandlers = new HashMap<>();

    public void registerRenderHandler(String widgetId, WidgetRenderHandler handler) {
        String id = requireWidgetId(widgetId);
        Objects.requireNonNull(handler, "handler must not be null");
        WidgetRenderHandler previous = renderHandlers.putIfAbsent(id, handler);
        if (previous != null) {
            throw new IllegalStateException("Widget render handler already registered: " + id);
        }
    }

    public void registerActionHandler(String widgetId, String action, WidgetActionHandler handler) {
        String id = requireWidgetId(widgetId);
        String actionName = requireAction(action);
        Objects.requireNonNull(handler, "handler must not be null");
        String key = actionKey(id, actionName);
        WidgetActionHandler previous = actionHandlers.putIfAbsent(key, handler);
        if (previous != null) {
            throw new IllegalStateException("Widget action handler already registered: " + key);
        }
    }

    public WidgetView render(String widgetId, ScreenContext context, Object input) {
        String id = requireWidgetId(widgetId);
        Objects.requireNonNull(context, "context must not be null");
        WidgetRenderHandler handler = renderHandlers.get(id);
        if (handler == null) {
            throw new IllegalStateException("Widget is not registered: " + id);
        }
        return handler.render(new WidgetContext(context, id, null, null), input);
    }

    public Optional<ScreenAction> handleCallback(String callbackData, ScreenContext context) {
        Objects.requireNonNull(context, "context must not be null");
        Optional<WidgetCallbackRef> parsed = parseCallback(callbackData);
        if (parsed.isEmpty()) {
            return Optional.empty();
        }

        WidgetCallbackRef ref = parsed.get();
        WidgetActionHandler handler = actionHandlers.get(actionKey(ref.widgetId(), ref.action()));
        if (handler == null) {
            return Optional.empty();
        }

        ScreenAction action = handler.handle(new WidgetContext(context, ref.widgetId(), ref.action(), ref.payload()));
        return Optional.ofNullable(action == null ? ScreenAction.handled() : action);
    }

    public static String encodeCallback(String widgetId, String action, String payload) {
        String id = requireWidgetId(widgetId);
        String actionName = requireAction(action);
        String safePayload = payload == null ? "" : payload;
        return CALLBACK_PREFIX + id + ":" + actionName + ":" + safePayload;
    }

    public static Optional<WidgetCallbackRef> parseCallback(String callbackData) {
        if (callbackData == null || callbackData.isBlank() || !callbackData.startsWith(CALLBACK_PREFIX)) {
            return Optional.empty();
        }
        String raw = callbackData.substring(CALLBACK_PREFIX.length());
        String[] parts = raw.split(":", 3);
        if (parts.length < 2) {
            return Optional.empty();
        }
        String payload = parts.length == 3 ? parts[2] : "";
        return Optional.of(new WidgetCallbackRef(parts[0], parts[1], payload));
    }

    private static String requireWidgetId(String widgetId) {
        Objects.requireNonNull(widgetId, "widgetId must not be null");
        if (widgetId.isBlank()) {
            throw new IllegalArgumentException("widgetId must not be blank");
        }
        return widgetId;
    }

    private static String requireAction(String action) {
        Objects.requireNonNull(action, "action must not be null");
        if (action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
        return action;
    }

    private static String actionKey(String widgetId, String action) {
        return widgetId + "::" + action;
    }

    public record WidgetCallbackRef(String widgetId, String action, String payload) {
    }

    @FunctionalInterface
    public interface WidgetRenderHandler {
        WidgetView render(WidgetContext context, Object input);
    }

    @FunctionalInterface
    public interface WidgetActionHandler {
        ScreenAction handle(WidgetContext context);
    }

    public record MethodBinding(Object bean, Method method) {
        public Object invoke(WidgetContext widgetContext, Object input) {
            Method targetMethod = method;
            Parameter[] parameters = targetMethod.getParameters();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = resolveParameter(parameters[i].getType(), widgetContext, input);
            }
            try {
                ReflectionUtils.makeAccessible(targetMethod);
                return targetMethod.invoke(bean, args);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access widget method: " + targetMethod, e);
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException() == null ? e : e.getTargetException();
                throw new IllegalStateException("Widget method failed: " + targetMethod, target);
            }
        }

        private Object resolveParameter(Class<?> type, WidgetContext widgetContext, Object input) {
            if (type == WidgetContext.class) {
                return widgetContext;
            }
            if (type == ScreenContext.class) {
                return widgetContext.screenContext();
            }
            if (type == CallbackQuery.class) {
                return widgetContext.screenContext().callbackQuery();
            }
            if (type == TelegramCallbackQuery.class) {
                return widgetContext.screenContext().telegramCallbackQuery();
            }
            if (input != null && type.isAssignableFrom(input.getClass())) {
                return input;
            }
            if (type == String.class) {
                return widgetContext.payload();
            }
            if (input == null) {
                return null;
            }
            throw new IllegalStateException("Unsupported widget parameter type: " + type.getName() + " for input " + input.getClass().getName());
        }
    }
}
