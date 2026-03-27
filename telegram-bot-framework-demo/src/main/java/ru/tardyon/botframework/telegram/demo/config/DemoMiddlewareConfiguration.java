package ru.tardyon.botframework.telegram.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;

@Configuration
public class DemoMiddlewareConfiguration {

    @Bean
    @Order(0)
    public UpdateMiddleware demoLoggingMiddleware() {
        return (updateContext, chain) -> {
            long startedAtNanos = System.nanoTime();
            updateContext.setAttribute("demo.startedAtNanos", startedAtNanos);
            try {
                chain.proceed(updateContext);
            } finally {
                long elapsedMicros = (System.nanoTime() - startedAtNanos) / 1_000L;
                System.out.println(
                    "[demo-middleware] updateType=" + updateContext.getUpdateType() + " elapsedMicros=" + elapsedMicros
                );
            }
        };
    }
}
