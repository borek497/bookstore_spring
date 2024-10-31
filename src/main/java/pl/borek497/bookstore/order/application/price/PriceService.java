package pl.borek497.bookstore.order.application.price;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.order.domain.Order;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceService {
    private final List<DiscountStrategy> strategies = List.of(
            new DeliveryDiscountStrategy(),
            new TotalPriceDiscountStrategy()
    );

    @Transactional
    public OrderPrice calculatePrice(Order order) {
        if (isItemsQuantityZeroOrNegative(order)) {
            throw new IllegalArgumentException("Order with 0 or negative quantity is prohibited");
        }
        return new OrderPrice(
                order.getItemsPrice(),
                order.getDeliveryPrice(),
                discounts(order)
        );
    }

    public BigDecimal discounts(Order order) {
        return strategies.stream()
                .map(strategy -> strategy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isItemsQuantityZeroOrNegative(Order order) {
        return order.getItems().stream().anyMatch(o -> o.getQuantity() <= 0);
    }
}