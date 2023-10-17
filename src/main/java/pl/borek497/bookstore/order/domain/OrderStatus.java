package pl.borek497.bookstore.order.domain;

public enum OrderStatus {
    NEW,
    CONFIRMED,
    IN_DELIVERY,
    DELIVERED,
    CANCELED,
    RETURNED
}