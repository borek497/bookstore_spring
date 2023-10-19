package pl.borek497.bookstore.order.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Recipient {
    String name;
    String phone;
    String street;
    String zipCode;
    String city;
    String email;
}
