import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Delivery
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.SELF_PICKUP


class PriceServiceSpecification extends Specification {

    PriceService priceService = new PriceService()

    def "should correct calculate total price for empty order"() {

        given:
        Order order = Order.builder().build();

        when:
        OrderPrice orderPrice = priceService.calculatePrice(order)

        then:
        orderPrice.finalPrice() == BigDecimal.ZERO
    }

    def "total price "() {
        given:
        Book harry = new Book("Harry Potter i Komnata tajemnic", 2001, BigDecimal.valueOf(50.00), 20)
        OrderItem harryItem = new OrderItem(harry, 5)
        Order order = Order.builder().item(harryItem).delivery(SELF_PICKUP).build()


        when:
        OrderPrice orderPrice = priceService.calculatePrice(order)

        then:
        orderPrice.finalPrice() == BigDecimal.valueOf(225.00)
    }
}