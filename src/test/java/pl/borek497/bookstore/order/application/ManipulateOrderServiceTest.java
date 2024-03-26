package pl.borek497.bookstore.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.db.BookJpaRepository;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ManipulateOrderServiceTest {

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jCip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jCip.getId(), 10))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);

        //then
        assertTrue(response.isSuccess());
        assertEquals(35L, catalogUseCase.findById(effectiveJava.getId()).get().getAvailable());
        assertEquals(40L, catalogUseCase.findById(jCip.getId()).get().getAvailable());
    }

    @Test
    public void userCanOrderMoreBooksThanAvailable() {
        //given
        Book effectiveJava = givenEffectiveJava(5L);
        Book jCip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .item(new OrderItemCommand(jCip.getId(), 10))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(command));

        //then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Book givenJavaConcurrency(long available) {
        return bookJpaRepository.save(new Book("Java Concurency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

}