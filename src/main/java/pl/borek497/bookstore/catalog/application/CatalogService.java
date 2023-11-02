package pl.borek497.bookstore.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.catalog.domain.CatalogRepository;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase.SaveUploadCommand;
import pl.borek497.bookstore.uploads.domain.Upload;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final CatalogRepository catalogRepository;
    private final UploadUseCase uploadUseCase;

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().contains(title))
                .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                .filter(book -> book.getAuthor().startsWith(author))
                .findFirst();
    }

    @Override
    public Book addBook(CreateBookCommand createBookCommand) {
        Book book = createBookCommand.toBook();
        return catalogRepository.save(book);
    }

    @Override
    public void removeById(Long id) {
        catalogRepository.removeById(id);

    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand) {
        return catalogRepository
                .findById(updateBookCommand.getId())
                .map(book -> {
                    Book updatedBook = updateBookCommand.updateFields(book);
                    catalogRepository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Arrays.asList("Book not found with id: " + updateBookCommand.getId())));
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand updateBookCoverCommand) {
        catalogRepository.findById(updateBookCoverCommand.getId())
                .ifPresent(book -> {
                    Upload savedUpload = uploadUseCase.save(new SaveUploadCommand(
                            updateBookCoverCommand.getFileName(),
                            updateBookCoverCommand.getFile(),
                            updateBookCoverCommand.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    catalogRepository.save(book);
                });
    }
}