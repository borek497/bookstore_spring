package pl.borek497.bookstore.order.domain;

import lombok.Value;

@Value
public class OrderItem {
    Long bookId;
    int quantity;
}