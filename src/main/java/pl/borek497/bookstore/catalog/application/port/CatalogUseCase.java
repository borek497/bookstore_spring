package pl.borek497.bookstore.catalog.application.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.borek497.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {

    List<Book> findAll();
    Optional<Book> findById(Long id);
    List<Book> findByTitle(String title);
    Optional<Book> findOneByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByTitleAndAuthor(String title, String author);

    Book addBook(CreateBookCommand createBookCommand);

    void removeById(Long id);

    UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand);

    void updateBookCover(UpdateBookCoverCommand updateBookCoverCommand);

    void removeBookCover(Long id);

    /**
     * Command to taki wrapper, takie opakowanie na pozostałe parametry
     * Takie klasy opakowujące możemy stworzyć albo w port, albo w interfejs.
     * Lepiej tworzyć takie klasy w interfejsie, mają bardzo krótki cykl życia, są tworzone na chwile, zaraz są niszczone.
     * Nie ma co zaśmiecac pakietu port klasami, które będą żyły chwile.
     *
     * @Value to taka adnotacja z lombooka, która sprawa, że pola są finalne i prywatne oraz mamy gotowy specjalny konstruktor
     * oraz gettery, settery, toString()
     */

    @Value
    class UpdateBookCoverCommand {
        Long id;
        byte[] file;
        String contentType;
        String fileName;
    }

    @Value
    class CreateBookCommand {
        String title;
        Set<Long> authors;
        Integer year;
        BigDecimal price;
        Long available;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateBookCommand {
        Long id;
        String title;
        Set<Long> authors;
        Integer year;
        BigDecimal price;
    }

    @Value
    class UpdateBookResponse {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());
        boolean success;
        List<String> errors;
    }
}