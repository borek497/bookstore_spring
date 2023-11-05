package pl.borek497.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalogUseCase;
    private final ManipulateOrderUseCase manipulateOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final String title;

    public ApplicationStartup(
            CatalogUseCase catalogUseCase,
            ManipulateOrderUseCase manipulateOrderUseCase,
            QueryOrderUseCase queryOrderUseCase,
            @Value("${bookstore.catalog.query}") String title) {
        this.catalogUseCase = catalogUseCase;
        this.manipulateOrderUseCase = manipulateOrderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.title = title;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void initData() {
        catalogUseCase.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("19.90")));
        catalogUseCase.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, new BigDecimal("29.90")));
        catalogUseCase.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1904, new BigDecimal("11.90")));
        catalogUseCase.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1834, new BigDecimal("14.90")));
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter i Kamień filozoficzny", "J.R. Rowlling", 1998, new BigDecimal("14.90")));
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter i Komnata tajemnic", "J.R. Rowlling", 2001, new BigDecimal("14.90")));
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter i Czara ognia", "J.R. Rowlling", 2004, new BigDecimal("14.90")));
    }

    private void searchCatalog() {
//        findByTitle();
//        findAndUpdate();
//        findByTitle();
    }

    private void placeOrder() {
        Book panTadeusz = catalogUseCase.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalogUseCase.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

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
                .item(new OrderItem(panTadeusz.getId(), 16))
                .item(new OrderItem(chlopi.getId(), 7))
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

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalogUseCase.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand updateBookCommand = UpdateBookCommand
                            .builder()
                            .id(book.getId())
                            .title("Pan Tadeusz czyli ostatni zajazd na Litwie")
                            .build();
                    UpdateBookResponse response =  catalogUseCase.updateBook(updateBookCommand);
                    System.out.println("Updating book result: " + response.isSuccess());
                });
    }

    private void findByTitle() {
        List<Book> panTadeusz = catalogUseCase.findByTitle(title);
        panTadeusz.forEach(System.out::println);
    }
}