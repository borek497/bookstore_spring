package pl.borek497.bookstore.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Book(String title, String author, Integer year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}