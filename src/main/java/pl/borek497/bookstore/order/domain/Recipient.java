package pl.borek497.bookstore.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipient {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String zipCode;
    private String city;
    private String email;

    public Recipient(String name, String phone, String street, String zipCode, String city, String email) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.email = email;
    }
}