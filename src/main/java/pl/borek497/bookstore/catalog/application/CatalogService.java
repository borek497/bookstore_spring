package pl.borek497.bookstore.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository;
import pl.borek497.bookstore.catalog.db.BookJpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase.SaveUploadCommand;
import pl.borek497.bookstore.uploads.domain.Upload;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository bookJpaRepository;
    private final AuthorJpaRepository authorJpaRepository;
    private final UploadUseCase uploadUseCase;

    @Override
    public List<Book> findByTitle(String title) {
        return bookJpaRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookJpaRepository.findById(id);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return bookJpaRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().contains(title))
                .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookJpaRepository.findAll()
                .stream()
                //.filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findAll()
                .stream()
                //.filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return bookJpaRepository.findAll();
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                //.filter(book -> book.getAuthor().startsWith(author))
                .findFirst();
    }

    @Override
    public Book addBook(CreateBookCommand createBookCommand) {
        Book book = toBook(createBookCommand);
        return bookJpaRepository.save(book);
    }

    private Book toBook(CreateBookCommand createBookCommand) {
        Book book = new Book(createBookCommand.getTitle(), createBookCommand.getYear(), createBookCommand.getPrice());
        Set<Author> authors = createBookCommand.getAuthors().stream()
                .map(authorId ->
                    authorJpaRepository
                            .findById(authorId)
                            .orElseThrow(() -> new IllegalArgumentException("Unable to find author with id: " + authorId))
                ).collect(Collectors.toSet());
        book.setAuthors(authors);
        return book;
    }

    @Override
    public void removeById(Long id) {
        bookJpaRepository.deleteById(id);

    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand) {
        return bookJpaRepository
                .findById(updateBookCommand.getId())
                .map(book -> {
                    Book updatedBook = updateBookCommand.updateFields(book);
                    bookJpaRepository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Arrays.asList("Book not found with id: " + updateBookCommand.getId())));
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand updateBookCoverCommand) {
        bookJpaRepository.findById(updateBookCoverCommand.getId())
                .ifPresent(book -> {
                    Upload savedUpload = uploadUseCase.save(new SaveUploadCommand(
                            updateBookCoverCommand.getFileName(),
                            updateBookCoverCommand.getFile(),
                            updateBookCoverCommand.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    bookJpaRepository.save(book);
                });
    }

    @Override
    public void removeBookCover(Long id) {
        bookJpaRepository.findById(id)
                .ifPresent(book -> {
                    if (book.getCoverId() != null) {
                        uploadUseCase.removeById(book.getCoverId());
                        book.setCoverId(null);
                        bookJpaRepository.save(book);
                    }
                });
    }
}