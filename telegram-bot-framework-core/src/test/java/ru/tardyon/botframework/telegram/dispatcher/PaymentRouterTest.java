package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingAddress;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class PaymentRouterTest {

    @Test
    void routesShippingAndPreCheckoutQueriesByPayloadFilters() {
        Router router = new Router();
        AtomicInteger shippingHits = new AtomicInteger();
        AtomicInteger preCheckoutHits = new AtomicInteger();

        router.shippingQuery(Filters.invoicePayloadEquals("physical:order-1"), (ctx, query) -> shippingHits.incrementAndGet());
        router.preCheckoutQuery(Filters.preCheckoutPayloadEquals("stars:order-1"), (ctx, query) -> preCheckoutHits.incrementAndGet());

        ShippingQuery shippingQuery = new ShippingQuery(
            "ship-q-1",
            new User(11L, false, "Ann", null, "ann", "en", null, null, null),
            "physical:order-1",
            new ShippingAddress("US", "CA", "San Francisco", "Market st", "Suite 100", "94103")
        );
        PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery(
            "pcq-1",
            new User(11L, false, "Ann", null, "ann", "en", null, null, null),
            "XTR",
            500,
            "stars:order-1",
            null,
            null
        );

        router.route(new UpdateContext(new Update(1L, null, null, null, null, null, shippingQuery, null, null, null)));
        router.route(new UpdateContext(new Update(2L, null, null, null, null, null, null, preCheckoutQuery, null, null)));

        assertEquals(1, shippingHits.get());
        assertEquals(1, preCheckoutHits.get());
    }
}
