package pl.borek497.bookstore.order.application;

import lombok.Value;
import pl.borek497.bookstore.order.application.price.OrderPrice;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public
class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice ordersPrice;
    BigDecimal finalPrice;
}