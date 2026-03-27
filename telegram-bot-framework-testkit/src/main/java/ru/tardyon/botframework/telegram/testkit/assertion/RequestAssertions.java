package ru.tardyon.botframework.telegram.testkit.assertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiRequest;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiServer;

/**
 * Framework-agnostic assertions for fake Bot API request verification.
 */
public final class RequestAssertions {

    private RequestAssertions() {
    }

    public static void assertMethodCalled(FakeBotApiServer server, String methodName) {
        assertMethodCalledTimes(server, methodName, 1);
    }

    public static void assertMethodCalledTimes(FakeBotApiServer server, String methodName, int times) {
        Objects.requireNonNull(server, "server must not be null");
        if (times < 0) {
            throw new IllegalArgumentException("times must be >= 0");
        }

        long count = server.recordedRequests(methodName).size();
        if (count != times) {
            throw new AssertionError("Expected method '" + methodName + "' to be called " + times + " times but was " + count);
        }
    }

    public static void assertRequestBody(FakeBotApiServer server, String methodName, Predicate<String> predicate, String description) {
        Objects.requireNonNull(server, "server must not be null");
        Objects.requireNonNull(predicate, "predicate must not be null");

        List<FakeBotApiRequest> calls = server.recordedRequests(methodName);
        if (calls.isEmpty()) {
            throw new AssertionError("Method '" + methodName + "' was not called");
        }

        String body = calls.get(calls.size() - 1).body();
        if (!predicate.test(body)) {
            throw new AssertionError("Request body assertion failed for '" + methodName + "': " + description + ". Body: " + body);
        }
    }

    public static void assertRequestBodyContains(FakeBotApiServer server, String methodName, String expectedSubstring) {
        assertRequestBody(
            server,
            methodName,
            body -> body != null && body.contains(expectedSubstring),
            "expected body to contain '" + expectedSubstring + "'"
        );
    }

    public static void assertCallOrder(FakeBotApiServer server, String... expectedMethods) {
        Objects.requireNonNull(server, "server must not be null");
        Objects.requireNonNull(expectedMethods, "expectedMethods must not be null");

        List<String> actual = server.recordedRequests().stream().map(FakeBotApiRequest::telegramMethod).toList();
        List<String> missing = new ArrayList<>();
        int currentIndex = 0;

        for (String expectedMethod : expectedMethods) {
            int found = indexOf(actual, expectedMethod, currentIndex);
            if (found < 0) {
                missing.add(expectedMethod);
                continue;
            }
            currentIndex = found + 1;
        }

        if (!missing.isEmpty()) {
            throw new AssertionError("Expected call order subsequence not satisfied. Missing: " + missing + "; actual: " + actual);
        }
    }

    private static int indexOf(List<String> values, String expected, int fromIndex) {
        for (int i = fromIndex; i < values.size(); i++) {
            if (Objects.equals(values.get(i), expected)) {
                return i;
            }
        }
        return -1;
    }
}
