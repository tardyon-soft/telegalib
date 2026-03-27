package ru.tardyon.botframework.telegram.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.diagnostics.BotApiRequestListener;
import ru.tardyon.botframework.telegram.diagnostics.BotApiResponseListener;
import ru.tardyon.botframework.telegram.diagnostics.ErrorListener;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingListener;

@Configuration
@ConditionalOnProperty(prefix = "demo.diagnostics", name = "enabled", havingValue = "true")
public class DemoDiagnosticsConfiguration {

    @Bean
    public BotApiRequestListener demoBotApiRequestListener() {
        return event -> System.out.println(
            "[demo-diagnostics] request method=" + event.methodName() +
                " correlationId=" + event.correlationId()
        );
    }

    @Bean
    public BotApiResponseListener demoBotApiResponseListener() {
        return event -> System.out.println(
            "[demo-diagnostics] response method=" + event.methodName() +
                " success=" + event.success() +
                " durationMs=" + event.durationMillis()
        );
    }

    @Bean
    public UpdateProcessingListener demoUpdateProcessingListener() {
        return new UpdateProcessingListener() {
            @Override
            public void onUpdateStarted(ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingStartedEvent event) {
                System.out.println(
                    "[demo-diagnostics] update-start id=" + event.updateId() +
                        " type=" + event.updateType() +
                        " source=" + event.source()
                );
            }

            @Override
            public void onUpdateFinished(ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingFinishedEvent event) {
                System.out.println(
                    "[demo-diagnostics] update-finish id=" + event.updateId() +
                        " type=" + event.updateType() +
                        " success=" + event.success() +
                        " durationMs=" + event.durationMillis()
                );
            }
        };
    }

    @Bean
    public ErrorListener demoErrorListener() {
        return event -> System.err.println(
            "[demo-diagnostics] error component=" + event.component() +
                " operation=" + event.operation() +
                " method=" + event.methodName() +
                " updateId=" + event.updateId() +
                " message=" + event.error().getMessage()
        );
    }
}
