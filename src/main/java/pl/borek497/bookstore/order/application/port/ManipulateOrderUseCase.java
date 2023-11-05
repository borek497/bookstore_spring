package pl.borek497.bookstore.order.application.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import pl.borek497.bookstore.commons.Either;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;

import java.util.List;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand placeOrderCommand);

    void deleteOrderById(Long id);

    void updateOrderStatus(Long id, OrderStatus status);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItem> items;
        Recipient recipient;
    }

    class PlaceOrderResponse extends Either<String, Long> {

        public PlaceOrderResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }
}