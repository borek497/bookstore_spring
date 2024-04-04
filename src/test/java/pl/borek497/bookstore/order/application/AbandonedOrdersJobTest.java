package pl.borek497.bookstore.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.db.BookJpaRepository;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.clock.Clock;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "app.orders.payment-period=1H"
)
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    AbandonedOrdersJob abandonedOrdersJob;

    @Autowired
    ManipulateOrderService manipulateOrderService;

    @Autowired
    QueryOrderService queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    Clock.Fake clock;

    @Test
    public void shouldMarkOrdersAsAbandoned() {
        // given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placedOrder(book.getId(), 15);

        // when - run
        clock.tick(Duration.ofHours(2));
        abandonedOrdersJob.run();


        //then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(book));
    }

    private Long placedOrder(Long bookId, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new ManipulateOrderUseCase.OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId()).get().getAvailable();
    }

}