package pl.borek497.bookstore.order.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private Long id;
    private List<OrderItem> items;

    private Recipient recipient;
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.NEW;
    private LocalDateTime createdAt;

    BigDecimal totalPrice() {
        return items.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}