package pl.borek497.bookstore.catalog.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.catalog.domain.CatalogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class CatalogService implements CatalogUseCase {

    private final CatalogRepository catalogRepository;

    public CatalogService(@Qualifier("bestsellerCatalogRepository") CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll()
                                .stream()
                                .filter(book -> book.getTitle().contains(title))
                                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return null;
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return Optional.empty();
    }

    @Override
    public void addBook() {

    }

    @Override
    public void removeById(Long id) {

    }

    @Override
    public void updateBook() {

    }
}