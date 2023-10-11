package pl.borek497.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.domain.Book;

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
        List<Book> panTadeusz = catalogUseCase.findByTitle(title);
        panTadeusz.forEach(System.out::println);
    }
}