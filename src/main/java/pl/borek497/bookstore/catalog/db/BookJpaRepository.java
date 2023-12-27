package pl.borek497.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.borek497.bookstore.catalog.domain.Book;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllEager();
    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainingIgnoreCase(String firstName, String lastName);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.firstName LIKE :name OR a.lastName LIKE :name")
    List<Book> findByAuthor(@Param("name") String name);
}