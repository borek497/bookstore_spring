package pl.borek497.bookstore.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
