package pl.borek497.bookstore.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.db.OrderJpaRepository;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderStatus;

@Service
@AllArgsConstructor
class ManipulateOrderService implements ManipulateOrderUseCase {

    private OrderJpaRepository orderJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand placeOrderCommand) {
        Order order = Order
                .builder()
                .recipient(placeOrderCommand.getRecipient())
                .items(placeOrderCommand.getItems())
                .build();

        Order save = orderJpaRepository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }

    @Override
    public void deleteOrderById(Long id) {
        orderJpaRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        orderJpaRepository.findById(id)
                .ifPresent(order -> {
                    order.updateStatus(status);
                    orderJpaRepository.save(order);
                });
    }
}