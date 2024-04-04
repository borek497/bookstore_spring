package pl.borek497.bookstore.order.application.port;

import lombok.*;
import pl.borek497.bookstore.commons.Either;
import pl.borek497.bookstore.order.domain.*;

import java.util.List;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand placeOrderCommand);

    void deleteOrderById(Long id);

    UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
        @Builder.Default
        Delivery delivery = Delivery.COURIER;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Value
    class UpdateStatusCommand {
        Long orderId;
        OrderStatus status;
        String email;
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

    class UpdateStatusResponse extends Either<String, OrderStatus> {

        public UpdateStatusResponse(boolean success, String left, OrderStatus right) {
            super(success, left, right);
        }

        public static UpdateStatusResponse success(OrderStatus orderStatus) {
            return new UpdateStatusResponse(true, null, orderStatus);
        }

        public static UpdateStatusResponse failure(String error) {
            return new UpdateStatusResponse(false, error, null);
        }
    }
}