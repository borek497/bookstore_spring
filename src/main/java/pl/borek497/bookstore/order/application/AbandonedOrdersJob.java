package pl.borek497.bookstore.order.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.borek497.bookstore.clock.Clock;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.borek497.bookstore.order.db.OrderJpaRepository;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {

    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;
    private final Clock clock;
    private final User systemUser;

    @Scheduled(cron = "${app.orders.abandon-cron}")
    @Transactional
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = clock.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, systemUser);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
