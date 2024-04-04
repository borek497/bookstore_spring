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
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.Delivery;
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
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "admin@example.org");
        service.updateOrderStatus(command);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderUseCase.findById(orderId).get().getStatus());
    }

    @Disabled
    public void userCannotRevokePaidOrder() {
        // user nie moze wycofac juz oplaconego zamowienia

        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placedOrder(effectiveJava.getId(), 15);
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "marek@example.com");

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateOrderStatus(command));

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

    @Test
    public void userCannotRevokeOtherUserOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, adam);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "marek@example.com");
        service.updateOrderStatus(command);

        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderUseCase.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCannotRevokeOtherUserOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, admin);
        service.updateOrderStatus(command);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderUseCase.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanMarkOrderAsPaid() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, admin);
        service.updateOrderStatus(command);

        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderUseCase.findById(orderId).get().getStatus());
    }

    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 1);

        // then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsAreDiscountedOver100Pln() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 3);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrdersPrice().getItemsPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPricedWhenTotalOver200Pln() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 5);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400Pln() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrder(book.getId(), 10);

        // then
        assertEquals("449.10", orderOf(orderId).getFinalPrice().toPlainString());
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Book givenJavaConcurrency(long available) {
        return bookJpaRepository.save(new Book("Java Concurency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email("john@example.org").build();
    }

    private Long placedOrder(Long bookId, int quantity, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, quantity))
                .delivery(Delivery.COURIER)
                .build();
        return service.placeOrder(command).getRight();
    }

    private Long placedOrder(Long bookId, int quantity) {
        return placedOrder(bookId, quantity, "john@example.org");
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId()).get().getAvailable();
    }

    private Book givenBook(long available, String price) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
    }

    private RichOrder orderOf(Long orderId) {
        return queryOrderUseCase.findById(orderId).get();
    }
}