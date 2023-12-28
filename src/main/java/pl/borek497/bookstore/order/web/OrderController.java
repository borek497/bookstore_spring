package pl.borek497.bookstore.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase.RichOrder;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.order.domain.Recipient;
import pl.borek497.bookstore.web.CreatedURI;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
class OrderController {

    private final ManipulateOrderUseCase manipulateOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;

    @GetMapping
    @ResponseStatus(OK)
    public List<RichOrder> getOrders() {
        return queryOrderUseCase.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrderUseCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody PlaceOrderCommand placeOrderCommand) {
        return manipulateOrderUseCase
                .placeOrder(placeOrderCommand)
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrder(@PathVariable Long id, @RequestBody UpdateStatusCommand updateStatusCommand) {
        OrderStatus orderStatus = OrderStatus
                .parseString(updateStatusCommand.status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + updateStatusCommand.status));
        manipulateOrderUseCase.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        queryOrderUseCase.deleteOrderById(id);
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}