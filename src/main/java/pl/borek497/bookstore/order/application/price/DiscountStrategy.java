package pl.borek497.bookstore.order.application.price;

import pl.borek497.bookstore.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
