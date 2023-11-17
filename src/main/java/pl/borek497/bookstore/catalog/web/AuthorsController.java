package pl.borek497.bookstore.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.borek497.bookstore.catalog.application.port.AuthorsUseCase;
import pl.borek497.bookstore.catalog.domain.Author;

import java.util.List;

@RestController
@RequestMapping("/{authors}")
@AllArgsConstructor
class AuthorsController {

    private final AuthorsUseCase authorsUseCase;

    @GetMapping
    public List<Author> findAll() {
        return authorsUseCase.findAll();
    }
}