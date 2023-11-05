package pl.borek497.bookstore.catalog.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import jakarta.persistence.Entity;


@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private String coverId;

    public Book(String title, String author, Integer year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}