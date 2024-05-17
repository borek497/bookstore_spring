package pl.borek497.bookstore.catalog.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.borek497.bookstore.catalog.domain.Book;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/catalog")
class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author
    ) {
        if (title.isPresent() && author.isPresent()) {
            return catalogUseCase.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalogUseCase.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalogUseCase.findByAuthor(author.get());
        } else {
            return catalogUseCase.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return catalogUseCase
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody CatalogController.RestBookCommand restBookCommand) {
        Book book = catalogUseCase.addBook(restBookCommand.toCreateCommand());
        URI uri = createBookUri(book);
        return ResponseEntity.created(uri).build();
    }

    private static URI createBookUri(Book book) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalogUseCase.removeById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand restBookCommand) {
        UpdateBookResponse response = catalogUseCase.updateBook(restBookCommand.toUpdateBookCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Got file: " + file.getOriginalFilename());
        catalogUseCase.updateBookCover(new UpdateBookCoverCommand(
                id,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()));
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalogUseCase.removeBookCover(id);
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title")
        private String title;

        @NotEmpty
        private Set<Long> authors;

        @NotNull
        private Integer year;

        @NotNull
        @PositiveOrZero
        private Long available;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateBookCommand(Long id) {
            return new UpdateBookCommand(id, title, authors, year, price);
        }
    }
}