package pl.borek497.bookstore.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.application.port.AuthorsUseCase;
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
class AuthorsService implements AuthorsUseCase {
    private final AuthorJpaRepository authorJpaRepository;

    public List<Author> findAll() {
//        System.out.println("AuthorsService.findAll() called");
//        System.out.println("Calling authorJpaRepository.findAll()");
        return authorJpaRepository.findAll();
    }
}
