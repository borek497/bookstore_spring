package pl.borek497.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;

import java.util.Optional;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
}