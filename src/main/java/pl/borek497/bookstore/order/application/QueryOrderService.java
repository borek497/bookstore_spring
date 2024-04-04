package pl.borek497.bookstore.order.application;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.application.price.OrderPrice;
import pl.borek497.bookstore.order.application.price.PriceService;
import pl.borek497.bookstore.order.db.OrderJpaRepository;
import pl.borek497.bookstore.order.domain.Order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository orderJpaRepository;
    private final PriceService priceService;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return orderJpaRepository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<RichOrder> findById(Long id) {
        return orderJpaRepository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        OrderPrice orderPrice = priceService.calculatePrice(order);
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt(),
                orderPrice,
                orderPrice.finalPrice()
        );
    }

    @Override
    public void deleteOrderById(Long id) {

    }
}