package pl.borek497.bookstore.order.application;

import org.junit.jupiter.api.Test;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RichOrderTest {

    @Test
    public void calculateTotalPriceOfEmptyOrder() {
        // given
        RichOrder richOrder = new RichOrder(
                1L,
                OrderStatus.NEW,
                Collections.emptySet(),
                Recipient.builder().build(),
                LocalDateTime.now()
        );

        // when
        BigDecimal price = richOrder.totalPrice();

        // then
        assertEquals(BigDecimal.ZERO, price);
    }

    @Test
    public void calculateTotalPrice() {
        // given
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        book2.setPrice(new BigDecimal("33.99"));

        Set<OrderItem> items = new HashSet<>(
                Arrays.asList(
                        new OrderItem(book1, 2),
                        new OrderItem(book2, 5)
                )
        );
        RichOrder richOrder = new RichOrder(
                1L,
                OrderStatus.NEW,
                items,
                Recipient.builder().build(),
                LocalDateTime.now()
        );

        // when
        BigDecimal price = richOrder.totalPrice();

        // then
        assertEquals(new BigDecimal("194.95"), price);
    }
}