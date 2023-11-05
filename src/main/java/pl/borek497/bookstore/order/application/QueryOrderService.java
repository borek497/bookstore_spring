package pl.borek497.bookstore.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.catalog.domain.CatalogRepository;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderRepository orderRepository;

    private final CatalogRepository catalogRepository;

    @Override
    public List<RichOrder> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RichOrder> findById(Long id) {
        return orderRepository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        List<RichOrderItem> richItems = toRichItems(order.getItems());
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                richItems,
                order.getRecipient(),
                order.getCreatedAt()
        );
    }

    private List<RichOrderItem> toRichItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = catalogRepository
                            .findById(item.getBookId())
                            .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
                    return new RichOrderItem(book, item.getQuantity());
                })
                .collect(Collectors.toList());
    }


    @Override
    public void deleteOrderById(Long id) {

    }
}