package pl.borek497.bookstore.order.domain;

import lombok.Value;
import pl.borek497.bookstore.catalog.domain.Book;

@Value
public class OrderItem {
    Book book;
    int quantity;
}