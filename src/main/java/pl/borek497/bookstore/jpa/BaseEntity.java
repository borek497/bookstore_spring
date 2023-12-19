package pl.borek497.bookstore.jpa;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String uuid = UUID.randomUUID().toString();
}