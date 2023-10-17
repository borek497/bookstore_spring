package pl.borek497.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.borek497.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalogUseCase;
    private final String title;

    public ApplicationStartup(
            CatalogUseCase catalogUseCase,
            @Value("${bookstore.catalog.query}") String title) {
        this.catalogUseCase = catalogUseCase;
        this.title = title;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void placeOrder() {

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
        catalogUseCase.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1897, new BigDecimal(10.99)));
        catalogUseCase.addBook(new CreateBookCommand("Ogniem i mieczem", "Henryk Sienkiewicz", 1898, new BigDecimal(12.99)));
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter i Komnata tajemnic", "J.R.R. Rownling", 1999, new BigDecimal(14.99)));
    }

    private void findByTitle() {
        List<Book> panTadeusz = catalogUseCase.findByTitle(title);
        panTadeusz.forEach(System.out::println);
    }
}