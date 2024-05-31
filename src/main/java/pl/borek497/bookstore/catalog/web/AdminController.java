package pl.borek497.bookstore.catalog.web;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.borek497.bookstore.catalog.application.port.CatalogInitializerUseCase;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Secured({"ROLE_ADMIN"})
class AdminController {

    private final CatalogInitializerUseCase initializer;

    @PostMapping("/initialization")
    @Transactional
    public void initialize() {
        initializer.initialize();
    }
}