package pl.borek497.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;
import pl.borek497.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CatalogControllerIT {

    //Mam tylko deklarację pól, one nie są nigdzie inicjowane, nie ma ich w konstruktorze. I tu wchodzi Spring
    @Autowired
    AuthorJpaRepository authorJpaRepository; //muszę dodać gdzieś autorów i mogę z jpa rozmawiać tutaj
    @Autowired
    CatalogController controller; //CatalogController.getAll() korzysta z CatalogUseCase a w środku ma addBook()
    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void getAllBooks() {

        //given
        givenEffectiveJava();
        givenJavaConcurencyInPractice();

        // when - Metoda CatalogController.getAll() pobiera nam wszystkie elementy
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());

        // then - lista nie powinna być pusta
        assertEquals(2, all.size());
    }


    @Test
    public void getBooksByAuthor() {

        //given
        givenEffectiveJava();
        givenJavaConcurencyInPractice();

        // when - Metoda CatalogController.getAll() pobiera nam wszystkie elementy
        List<Book> all = controller.getAll(Optional.empty(), Optional.of("Bloch"));

        // then - lista nie powinna być pusta
        assertEquals(1, all.size());
        assertEquals("EffectiveJava", all.get(0).getTitle());
    }

    @Test
    public void getBooksByTitle() {

        //given
        givenEffectiveJava();
        givenJavaConcurencyInPractice();

        // when - Metoda CatalogController.getAll() pobiera nam wszystkie elementy
        List<Book> all = controller.getAll(Optional.of("Java Concurrency in Practice"), Optional.empty());

        // then - lista nie powinna być pusta
        assertEquals(1, all.size());
        assertEquals("Java Concurrency in Practice", all.get(0).getTitle());
    }

    private void givenEffectiveJava() {
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "EffectiveJava",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
    }

    private void givenJavaConcurencyInPractice() {
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
    }
}