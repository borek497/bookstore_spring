package pl.borek497.bookstore.order.application.port;

import pl.borek497.bookstore.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {

    List<Order> findAll();
}