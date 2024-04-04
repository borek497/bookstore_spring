package pl.borek497.bookstore.order.application.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {

    BigDecimal itemsPrice;      //całościowa cena za wszystkie elementy
    BigDecimal deliveryPrice;   //jaka jest cena przesyłki, niezależnie od tego czy ktoś się łapie czy nie
    BigDecimal discounts;       //wartość rabatów

    public BigDecimal finalPrice() {
        return itemsPrice.add(deliveryPrice).subtract(discounts);
    }
}