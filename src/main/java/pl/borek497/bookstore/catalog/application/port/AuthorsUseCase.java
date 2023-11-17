package pl.borek497.bookstore.catalog.application.port;

import pl.borek497.bookstore.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {
    List<Author> findAll();
}
