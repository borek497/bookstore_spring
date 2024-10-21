import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Delivery
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.COURIER
import static pl.borek497.bookstore.order.domain.Delivery.SELF_PICKUP


class PriceServiceSpecification extends Specification {

    PriceService priceService = new PriceService()

    def "should correct calculate total price for empty order"() {

        given:
        Order emptyOrder = Order.builder().build();

        when:
        OrderPrice emptyOrderPrice = priceService.calculatePrice(emptyOrder)

        then:
        emptyOrderPrice.finalPrice() == BigDecimal.ZERO
    }

    def "should correct count final price for self pickup delivery"() {

        given:
        Book harryChamberOfSecrets = new Book(
                "Harry Potter i Komnata tajemnic",
                1998,
                BigDecimal.valueOf(50.00
                ),
                20
        )
        OrderItem harryChamberItem = new OrderItem(harryChamberOfSecrets, 5)
        Order harryChamberOrder = Order.builder().item(harryChamberItem).delivery(SELF_PICKUP).build()


        when:
        OrderPrice harryChamberPrice = priceService.calculatePrice(harryChamberOrder)

        then:
        harryChamberPrice.finalPrice() == BigDecimal.valueOf(225.00)
    }

    def "should correct count final price when order price grater than 400 PLN"() {
        given:
        Book harryGobletOfFire = new Book(
                "Harry Potter i Czara Ognia",
                2000,
                BigDecimal.valueOf(100.00
                ),
                100)

        OrderItem harryGobletOfFireItem = new OrderItem(harryGobletOfFire, 10)
        Order harryGobletOfFireOrder = Order.builder().item(harryGobletOfFireItem).delivery(COURIER).build();

        when:
        OrderPrice harryGobletPrice = priceService.calculatePrice(harryGobletOfFireOrder)

        then:
        harryGobletPrice.finalPrice() == BigDecimal.valueOf(900.00)
    }
    //inne testy wyliczania znizki
}