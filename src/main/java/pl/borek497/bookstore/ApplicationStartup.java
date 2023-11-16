package pl.borek497.bookstore;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

@Component
@AllArgsConstructor
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalogUseCase;
    private final ManipulateOrderUseCase manipulateOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final AuthorJpaRepository authorJpaRepository;

    @Override
    public void run(String... args) {
        initData();
        placeOrder();
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00")
        );

        CreateBookCommand javaPuzzlers = new CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00")
        );
        catalogUseCase.addBook(effectiveJava);
        catalogUseCase.addBook(javaPuzzlers);
    }

    private void placeOrder() {
        Book effectiveJava = catalogUseCase.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book javaPuzzlers = catalogUseCase.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Adama Mickiewicza 12A")
                .city("Świebodzin")
                .zipCode("66-200")
                .email("jan@ms.com")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(effectiveJava.getId(), 16))
                .item(new OrderItem(javaPuzzlers.getId(), 7))
                .build();

        PlaceOrderResponse placeOrderResponse = manipulateOrderUseCase.placeOrder(command);
        String result = placeOrderResponse.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        System.out.println(result);

        queryOrderUseCase.findAll()
                .forEach(order -> System.out.println("Got order with total price: " + order.totalPrice() + " details: " + order));
    }
}