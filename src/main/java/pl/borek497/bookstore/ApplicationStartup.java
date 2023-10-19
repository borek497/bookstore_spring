package pl.borek497.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.order.application.port.PlaceOrderUseCase;
import pl.borek497.bookstore.order.application.port.PlaceOrderUseCase.PlaceOrderCommand;
import pl.borek497.bookstore.order.application.port.PlaceOrderUseCase.PlaceOrderResponse;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.OrderItem;
import pl.borek497.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalogUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final String title;

    public ApplicationStartup(
            CatalogUseCase catalogUseCase,
            PlaceOrderUseCase placeOrderUseCase,
            QueryOrderUseCase queryOrderUseCase,
            @Value("${bookstore.catalog.query}") String title) {
        this.catalogUseCase = catalogUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.title = title;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void placeOrder() {
        //find pan Tadeusz
        Book panTadeusz = catalogUseCase.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        //find chlopi
        Book chlopi = catalogUseCase.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        //create recipient

        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Adama Mickiewicza 12A")
                .city("Świebodzin")
                .zipCode("66-200")
                .email("jan@ms.com")
                .build();


        //place order command
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(panTadeusz, 16))
                .item(new OrderItem(chlopi, 7))
                .build();

        PlaceOrderResponse placeOrderResponse = placeOrderUseCase.placeOrder(command);
        System.out.println("Created order with id: " + placeOrderResponse.getOrderId());

        //list all orders
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

    private void initData() {
        catalogUseCase.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1897, BigDecimal.valueOf(10.99)));
        catalogUseCase.addBook(new CreateBookCommand("Ogniem i mieczem", "Henryk Sienkiewicz", 1898, BigDecimal.valueOf(12.99)));
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter i Komnata tajemnic", "J.R.R. Rownling", 1999, BigDecimal.valueOf(14.99)));
        catalogUseCase.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1777, BigDecimal.valueOf(44.99)));
    }

    private void findByTitle() {
        List<Book> panTadeusz = catalogUseCase.findByTitle(title);
        panTadeusz.forEach(System.out::println);
    }
}