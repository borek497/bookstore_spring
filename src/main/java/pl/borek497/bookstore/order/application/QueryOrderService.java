package pl.borek497.bookstore.order.application;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.db.BookJpaRepository;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.db.OrderJpaRepository;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository orderJpaRepository;
    private final BookJpaRepository bookJpaRepository;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return orderJpaRepository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RichOrder> findById(Long id) {
        return orderJpaRepository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt()
        );
    }

    @Override
    public void deleteOrderById(Long id) {

    }
}