package pl.borek497.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.borek497.bookstore.catalog.application.CatalogController;
import pl.borek497.bookstore.catalog.domain.Book;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;
    private final String title;

    public ApplicationStartup(
            CatalogController catalogController,
            @Value("${bookstore.catalog.query}") String title) {
        this.catalogController = catalogController;
        this.title = title;
    }
    @Override
    public void run(String... args) {
        List<Book> panTadeusz = catalogController.findByTitle(title);
        panTadeusz.forEach(System.out::println);
    }
}