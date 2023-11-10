package pl.borek497.bookstore.order.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Recipient {
    private String name;
    private String phone;
    private String street;
    private String zipCode;
    private String city;
    private String email;
}
