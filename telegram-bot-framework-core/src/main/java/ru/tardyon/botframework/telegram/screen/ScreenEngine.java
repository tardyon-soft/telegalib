package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import java.util.Optional;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.StateKey;

public final class ScreenEngine {

    private final ScreenRegistry screenRegistry;
    private final ScreenStateStorage screenStateStorage;
    private final ScreenViewRenderer screenViewRenderer;

    public ScreenEngine(ScreenRegistry screenRegistry, ScreenStateStorage screenStateStorage) {
        this(screenRegistry, screenStateStorage, new DefaultTelegramScreenViewRenderer());
    }

    public ScreenEngine(
        ScreenRegistry screenRegistry,
        ScreenStateStorage screenStateStorage,
        ScreenViewRenderer screenViewRenderer
    ) {
        this.screenRegistry = Objects.requireNonNull(screenRegistry, "screenRegistry must not be null");
        this.screenStateStorage = Objects.requireNonNull(screenStateStorage, "screenStateStorage must not be null");
        this.screenViewRenderer = Objects.requireNonNull(screenViewRenderer, "screenViewRenderer must not be null");
    }

    public void start(UpdateContext updateContext, String screenId) {
        Objects.requireNonNull(updateContext, "updateContext must not be null");
        String targetScreenId = requireScreenId(screenId);
        ScreenKey key = resolveKey(updateContext)
            .orElseThrow(() -> new IllegalStateException("Cannot resolve screen key from update"));
        long chatId = key.chatId();

        ScreenStack stack = new ScreenStack();
        stack.push(targetScreenId);
        screenStateStorage.save(key, stack);
        renderCurrent(updateContext, key, chatId);
    }

    public boolean handle(UpdateContext updateContext) {
        Objects.requireNonNull(updateContext, "updateContext must not be null");
        if (updateContext.getMessage() == null && updateContext.getCallbackQuery() == null) {
            return false;
        }

        Optional<ScreenKey> keyOptional = resolveKey(updateContext);
        if (keyOptional.isEmpty()) {
            return false;
        }
        ScreenKey key = keyOptional.get();
        ScreenStack stack = screenStateStorage.find(key).orElse(null);
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        ScreenFrame frame = stack.current().orElse(null);
        if (frame == null) {
            return false;
        }

        Screen screen = screenRegistry.find(frame.screenId()).orElse(null);
        if (screen == null) {
            screenStateStorage.clear(key);
            return false;
        }

        long chatId = key.chatId();
        ScreenNavigator navigator = new DefaultScreenNavigator(updateContext, key, chatId);
        ScreenContext screenContext = new ScreenContext(updateContext, new ScreenStateContext(screenStateStorage, key), navigator);
        ScreenAction action = invokeAction(screen, screenContext, updateContext);
        if (action == null || action.kind() == ScreenAction.Kind.UNHANDLED) {
            return false;
        }
        applyAction(updateContext, key, chatId, action);
        return true;
    }

    private ScreenAction invokeAction(Screen screen, ScreenContext context, UpdateContext updateContext) {
        Message message = updateContext.getMessage();
        if (message != null) {
            return screen.onMessage(context, message);
        }
        CallbackQuery callbackQuery = updateContext.getCallbackQuery();
        if (callbackQuery != null) {
            return screen.onCallbackQuery(context, callbackQuery);
        }
        return ScreenAction.unhandled();
    }

    private void applyAction(UpdateContext updateContext, ScreenKey key, long chatId, ScreenAction action) {
        ScreenStack stack = screenStateStorage.getOrCreate(key);
        switch (action.kind()) {
            case HANDLED -> {
                return;
            }
            case RENDER -> renderCurrent(updateContext, key, chatId);
            case PUSH -> {
                stack.push(requireScreenId(action.targetScreenId()));
                renderCurrent(updateContext, key, chatId);
            }
            case REPLACE -> {
                stack.replace(requireScreenId(action.targetScreenId()));
                renderCurrent(updateContext, key, chatId);
            }
            case BACK -> {
                stack.back();
                renderCurrent(updateContext, key, chatId);
            }
            case CLEAR -> screenStateStorage.clear(key);
            case UNHANDLED -> {
                return;
            }
        }
    }

    private void renderCurrent(UpdateContext updateContext, ScreenKey key, long chatId) {
        ScreenStack stack = screenStateStorage.getOrCreate(key);
        ScreenFrame frame = stack.current()
            .orElseThrow(() -> new IllegalStateException("No active screen frame for key: " + key));
        Screen screen = screenRegistry.getRequired(frame.screenId());
        ScreenNavigator navigator = new DefaultScreenNavigator(updateContext, key, chatId);
        ScreenContext screenContext = new ScreenContext(updateContext, new ScreenStateContext(screenStateStorage, key), navigator);
        ScreenView screenView = screen.render(screenContext);
        screenViewRenderer.render(updateContext, screenContext.screenState(), chatId, screenView);
    }

    private Optional<ScreenKey> resolveKey(UpdateContext updateContext) {
        StateKey stateKey = updateContext.state().key().orElse(null);
        if (stateKey != null) {
            return Optional.of(new ScreenKey(stateKey.botId(), stateKey.chatId()));
        }
        return ScreenKeyResolver.resolve(updateContext.getUpdate(), "default-bot");
    }

    private String requireScreenId(String screenId) {
        Objects.requireNonNull(screenId, "screenId must not be null");
        if (screenId.isBlank()) {
            throw new IllegalArgumentException("screenId must not be blank");
        }
        return screenId;
    }

    private final class DefaultScreenNavigator implements ScreenNavigator {
        private final UpdateContext updateContext;
        private final ScreenKey key;
        private final long chatId;

        private DefaultScreenNavigator(UpdateContext updateContext, ScreenKey key, long chatId) {
            this.updateContext = Objects.requireNonNull(updateContext, "updateContext must not be null");
            this.key = Objects.requireNonNull(key, "key must not be null");
            this.chatId = chatId;
        }

        @Override
        public void push(String screenId) {
            ScreenStack stack = screenStateStorage.getOrCreate(key);
            stack.push(requireScreenId(screenId));
            ScreenEngine.this.renderCurrent(updateContext, key, chatId);
        }

        @Override
        public void replace(String screenId) {
            ScreenStack stack = screenStateStorage.getOrCreate(key);
            stack.replace(requireScreenId(screenId));
            ScreenEngine.this.renderCurrent(updateContext, key, chatId);
        }

        @Override
        public boolean back() {
            ScreenStack stack = screenStateStorage.getOrCreate(key);
            boolean movedBack = stack.back();
            ScreenEngine.this.renderCurrent(updateContext, key, chatId);
            return movedBack;
        }

        @Override
        public void clear() {
            screenStateStorage.clear(key);
        }

        @Override
        public Optional<String> currentScreenId() {
            return screenStateStorage.find(key)
                .flatMap(ScreenStack::current)
                .map(ScreenFrame::screenId);
        }

        @Override
        public void renderCurrent() {
            ScreenEngine.this.renderCurrent(updateContext, key, chatId);
        }
    }
}
