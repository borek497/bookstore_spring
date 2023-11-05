package pl.borek497.bookstore.order.domain;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {
    String name;
    String phone;
    String street;
    String zipCode;
    String city;
    String email;
}
