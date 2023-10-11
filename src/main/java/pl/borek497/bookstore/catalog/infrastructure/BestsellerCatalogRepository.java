package pl.borek497.bookstore.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BestsellerCatalogRepository implements CatalogRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public BestsellerCatalogRepository() {
        storage.put(0L, new Book(0L, "Pan Tadeusz", "Adam Mickiewicz", 1897));
        storage.put(1L, new Book(1L, "Ogniem i mieczem", "Henryk Sienkiewicz", 1898));
        storage.put(2L, new Book(2L, "Harry Potter i Komnata tajemnic", "J.R.R. Rownling", 1999));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}