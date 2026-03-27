package ru.tardyon.botframework.telegram.spring.boot.widget;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.ReflectionUtils;
import ru.tardyon.botframework.telegram.screen.ScreenAction;

public final class TelegramWidgetAnnotationRegistrar implements SmartInitializingSingleton {

    private final AnnotatedWidgetRegistry widgetRegistry;
    private final ListableBeanFactory beanFactory;

    public TelegramWidgetAnnotationRegistrar(AnnotatedWidgetRegistry widgetRegistry, ListableBeanFactory beanFactory) {
        this.widgetRegistry = Objects.requireNonNull(widgetRegistry, "widgetRegistry must not be null");
        this.beanFactory = Objects.requireNonNull(beanFactory, "beanFactory must not be null");
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(WidgetController.class);
        for (Object controller : controllers.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(controller);
            ReflectionUtils.doWithMethods(targetClass, method -> registerMethod(controller, method));
        }
    }

    private void registerMethod(Object bean, Method method) {
        Widget widget = method.getAnnotation(Widget.class);
        if (widget != null) {
            validateWidgetRenderMethod(method);
            AnnotatedWidgetRegistry.MethodBinding binding = new AnnotatedWidgetRegistry.MethodBinding(bean, method);
            widgetRegistry.registerRenderHandler(widget.id(), (context, input) -> {
                Object result = binding.invoke(context, input);
                if (!(result instanceof WidgetView widgetView)) {
                    throw new IllegalStateException("Widget method must return WidgetView: " + method);
                }
                return widgetView;
            });
        }

        OnWidgetAction widgetAction = method.getAnnotation(OnWidgetAction.class);
        if (widgetAction != null) {
            validateWidgetActionMethod(method);
            AnnotatedWidgetRegistry.MethodBinding binding = new AnnotatedWidgetRegistry.MethodBinding(bean, method);
            widgetRegistry.registerActionHandler(widgetAction.widget(), widgetAction.action(), context -> {
                Object result = binding.invoke(context, context.payload());
                if (result == null) {
                    return ScreenAction.handled();
                }
                if (result instanceof ScreenAction action) {
                    return action;
                }
                throw new IllegalStateException("Widget action method must return ScreenAction or void: " + method);
            });
        }
    }

    private void validateWidgetRenderMethod(Method method) {
        if (method.getReturnType() != WidgetView.class) {
            throw new IllegalStateException("Widget render method must return WidgetView: " + method);
        }
    }

    private void validateWidgetActionMethod(Method method) {
        if (method.getReturnType() != Void.TYPE && method.getReturnType() != ScreenAction.class) {
            throw new IllegalStateException("Widget action method must return ScreenAction or void: " + method);
        }

        for (Parameter parameter : method.getParameters()) {
            Class<?> type = parameter.getType();
            if (type == WidgetContext.class
                || type == ru.tardyon.botframework.telegram.screen.ScreenContext.class
                || type == ru.tardyon.botframework.telegram.api.model.CallbackQuery.class
                || type == ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery.class
                || type == String.class) {
                continue;
            }
            throw new IllegalStateException("Unsupported widget action parameter type: " + type.getName() + " in " + method);
        }
    }
}
