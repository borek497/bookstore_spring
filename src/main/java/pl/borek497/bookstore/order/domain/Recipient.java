package pl.borek497.bookstore.order.domain;

import jakarta.persistence.Entity;
import lombok.*;
import pl.borek497.bookstore.jpa.BaseEntity;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipient extends BaseEntity {

    private String email;
    private String name;
    private String phone;
    private String street;
    private String zipCode;
    private String city;
}