package pl.borek497.bookstore.catalog.application;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.borek497.bookstore.catalog.application.port.CatalogInitializerUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository;
import pl.borek497.bookstore.catalog.domain.Author;
import pl.borek497.bookstore.catalog.domain.Book;
import pl.borek497.bookstore.jpa.BaseEntity;
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase;
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase;
import pl.borek497.bookstore.order.domain.Recipient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CatalogInitializerService implements CatalogInitializerUseCase {

    private final ManipulateOrderUseCase manipulateOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final AuthorJpaRepository authorJpaRepository;
    private final CatalogUseCase catalogUseCase;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void initData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> build = new CsvToBeanBuilder<CsvBook>(reader)
                    .withType(CsvBook.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            build.stream().forEach(this::initBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV file", e);
        }
    }

    private void initBook(CsvBook csvBook) {
        Set<Long> authors = Arrays
                .stream(csvBook.authors.split(","))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(this::getOrCreateAuthor)
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());

        CreateBookCommand command = new CreateBookCommand(
                csvBook.title,
                authors,
                csvBook.year,
                csvBook.amount,
                50L);
        Book book = catalogUseCase.addBook(command);
        catalogUseCase.updateBookCover(updateBookCoverCommand(book.getId(), csvBook.thumbnail));
    }

    private UpdateBookCoverCommand updateBookCoverCommand(Long bookId, String thumbnailUrl) {
        ResponseEntity<byte[]> response = restTemplate.exchange(thumbnailUrl, HttpMethod.GET, null, byte[].class);
        String contentType = response.getHeaders().getContentType().toString();
        return new UpdateBookCoverCommand(bookId, response.getBody(), contentType, "cover");
    }

    private Author getOrCreateAuthor(String name) {
        return authorJpaRepository
                .findByNameIgnoreCase(name)
                .orElseGet(() -> authorJpaRepository.save(new Author(name)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {

        @CsvBindByName
        private String title;

        @CsvBindByName
        private String authors;

        @CsvBindByName
        private Integer year;

        @CsvBindByName
        private BigDecimal amount;

        @CsvBindByName
        private String thumbnail;
    }

    private void placeOrder() {
        Book effectiveJava = catalogUseCase.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book javaPuzzlers = catalogUseCase.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Adama Mickiewicza 12A")
                .city("Świebodzin")
                .zipCode("66-200")
                .email("jan@ms.com")
                .build();

        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new ManipulateOrderUseCase.OrderItemCommand(effectiveJava.getId(), 16))
                .item(new ManipulateOrderUseCase.OrderItemCommand(javaPuzzlers.getId(), 7))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse placeOrderResponse = manipulateOrderUseCase.placeOrder(command);
        String result = placeOrderResponse.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        log.info(result);

        queryOrderUseCase.findAll()
                .forEach(order -> log.info("Got order with total price: " + order.totalPrice() + " details: " + order));
    }
}