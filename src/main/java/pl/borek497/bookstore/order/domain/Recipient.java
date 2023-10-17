package pl.borek497.bookstore.order.domain;

import lombok.Value;

@Value
public class Recipient {
    String name;
    String phone;
    String street;
    String zipCode;
    String city;
    String email;
}
