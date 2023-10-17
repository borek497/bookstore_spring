package pl.borek497.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.order.application.port.PlaceOrderUseCase;
import pl.borek497.bookstore.order.domain.Order;
import pl.borek497.bookstore.order.domain.OrderRepository;

@Service
@RequiredArgsConstructor
class PlaceOrderService implements PlaceOrderUseCase {

    private OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand placeOrderCommand) {
        Order order = Order
                .builder()
                .recipient(placeOrderCommand.getRecipient())
                .items(placeOrderCommand.getItems())
                .build();

        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }
}