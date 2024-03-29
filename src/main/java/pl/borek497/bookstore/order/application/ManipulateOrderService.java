package pl.borek497.bookstore.order.application;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.db.BookJpaRepository;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.db.OrderJpaRepository;
import pl.borek497.bookstore.order.db.RecipientJpaRepository;
import pl.borek497.bookstore.order.domain.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
class ManipulateOrderService implements ManipulateOrderUseCase {

    private OrderJpaRepository orderJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand placeOrderCommand) {
        Set<OrderItem> items = placeOrderCommand
                .getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());

        Order order = Order
                .builder()
                .recipient(getOrCreateRecipient(placeOrderCommand.getRecipient()))
                .items(items)
                .build();

        Order savedOrder = orderJpaRepository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand orderItemCommand) {
        Book book = bookJpaRepository.getOne(orderItemCommand.getBookId());
        int quantity = orderItemCommand.getQuantity();

        if (quantity <= 0) {
            throw new IllegalArgumentException("Cannot place order with 0 or negative quantity");
        }

        if (book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }

        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: "
                + quantity + " of " + book.getAvailable() + " available");
    }

    @Override
    public void deleteOrderById(Long id) {
        orderJpaRepository.deleteById(id);
    }

    @Override
    public UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return orderJpaRepository.findById(command.getOrderId())
                .map(order -> {
                    if (!hasAccess(command, order)) {
                        return UpdateStatusResponse.failure("Unauthorized");
                    }

                    UpdateStatusResult result = order.updateStatus(command.getStatus());
                    if (result.isRevoked()) {
                        bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                    }
                    orderJpaRepository.save(order);
                    return UpdateStatusResponse.success(order.getStatus());
                })
                .orElse(UpdateStatusResponse.failure("Order not found"));
    }

    private static boolean hasAccess(UpdateStatusCommand command, Order order) {
        String email = command.getEmail();
        return email.equalsIgnoreCase(order.getRecipient().getEmail()) ||
                email.equalsIgnoreCase("admin@example.org");
    }
}