package pl.borek497.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}