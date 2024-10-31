import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.COURIER


class CatalogSpecificationTest extends Specification {

    //Potrzebujemy mock
    def "ksiażka o zbyt niskim nakładzie (available >= 0)"() {

        given:
        Book book = new Book(
                "Harry Potter i Komnata tajemnic", 1998, BigDecimal.valueOf(50), 0)
        OrderItem book2 = new OrderItem(book, 1)
        Order order2 = Order.builder().item(book2).delivery(COURIER).build()

        when:
        OrderPrice orderPrice2 = priceService.calculatePrice(order2)

        then:
        orderPrice2.deliveryPrice == BigDecimal.valueOf(9.90)
        orderPrice2.discounts == BigDecimal.ZERO
        orderPrice2.finalPrice() == BigDecimal.valueOf(59.90)
    }
}