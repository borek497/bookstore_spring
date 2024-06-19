package pl.borek497.bookstore.order.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.borek497.bookstore.order.application.RichOrder;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.OrderStatus;
import pl.borek497.bookstore.security.UserSecurity;
import pl.borek497.bookstore.web.CreatedURI;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
class OrderController {

    private final ManipulateOrderUseCase manipulateOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    @ResponseStatus(OK)
    public List<RichOrder> getOrders() {
        return queryOrderUseCase.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return queryOrderUseCase.findById(id)
                .map(order -> authorize(order, user))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorize(RichOrder order, UserDetails user) {
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(FORBIDDEN).build();
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

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + status));
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, user);
        return manipulateOrderUseCase.updateOrderStatus(command)
                .handle(
                        newStatus -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.status(error.getStatus()).build()
                );
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        queryOrderUseCase.deleteOrderById(id);
    }
}