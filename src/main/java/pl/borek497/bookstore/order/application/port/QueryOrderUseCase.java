package pl.borek497.bookstore.order.application.port;

import pl.borek497.bookstore.order.application.RichOrder;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {

    List<RichOrder> findAll();

    Optional<RichOrder> findById(Long id);

    void deleteOrderById(Long id);
}