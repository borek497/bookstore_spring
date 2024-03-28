package pl.borek497.bookstore.order.application;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
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
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    QueryOrderUseCase queryOrderUseCase;

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
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jCip));
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

    @Test
    public void userCanRevokeOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placedOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        service.updateOrderStatus(orderId, OrderStatus.CANCELED);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderUseCase.findById(orderId).get().getStatus());
    }

    @Test
    public void userCannotRevokePaidOrder() {
        // user nie moze wycofac juz oplaconego zamowienia

        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placedOrder(effectiveJava.getId(), 15);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () ->service.updateOrderStatus(orderId, OrderStatus.CANCELED));

        // then
        assertTrue(exception.getMessage().contains("Unable to mark PAID order as CANCELED"));
    }

    @Disabled
    public void userCannotRevokeShippedOrder() {
        // user nie moze wycofac juz wyslanego zamowienia
    }

    @Test
    public void userCannotOrderNoExistingBooks() { // 4 test

        // given
        Long randomId = RandomGenerator.getDefault().nextLong();
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(randomId, 10))
                .build();

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.placeOrder(placeOrderCommand));

        // then
        assertTrue(exception.getMessage().contains("Unable to find " + Book.class.getPackageName() + ".Book with id " + randomId));
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() { // 5 test

        // given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), -1))
                .build();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.placeOrder(placeOrderCommand));

        // then
        assertTrue(exception.getMessage().contains("Cannot place order with 0 or negative quantity"));
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

    private Long placedOrder(Long bookId, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return service.placeOrder(command).getRight();
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId()).get().getAvailable();
    }
}