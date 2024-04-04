package pl.borek497.bookstore.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum Delivery {
    SELF_PICKUP(BigDecimal.ZERO),
    COURIER(new BigDecimal("9.90"));

    private final BigDecimal price;
}