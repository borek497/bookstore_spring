package pl.borek497.bookstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.borek497.bookstore.catalog.application.CatalogController;
import pl.borek497.bookstore.catalog.domain.CatalogService;
import pl.borek497.bookstore.catalog.domain.Book;

import java.util.List;

@SpringBootApplication
public class OnlineBookstoreApplication  {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookstoreApplication.class, args);
	}
}