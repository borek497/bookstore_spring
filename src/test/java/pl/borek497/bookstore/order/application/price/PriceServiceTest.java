package pl.borek497.bookstore.order.application.price;

import org.junit.jupiter.api.Test;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderItem;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceServiceTest {

    PriceService priceService = new PriceService();


    @Test
    public void calculateTotalPriceOfEmptyOrder() {
        // given
        Order order = Order
                .builder()
                .build();

        // when
        OrderPrice price = priceService.calculatePrice(order);

        // then
        assertEquals(BigDecimal.ZERO, price.finalPrice());
    }

    @Test
    public void calculateTotalPrice() {
        // given
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        book2.setPrice(new BigDecimal("33.99"));

        Order order = Order
                .builder()
                .item(new OrderItem(book1, 2))
                .item(new OrderItem(book2, 5))
                .build();

        // when
        OrderPrice price = priceService.calculatePrice(order);

        // then
        assertEquals(new BigDecimal("194.95"), price.finalPrice());
        assertEquals(new BigDecimal("194.95"), price.getItemsPrice());
    }
}