import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Delivery
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Shared
import spock.lang.Specification


import static java.math.BigDecimal.ZERO

import static java.math.BigDecimal.valueOf
import static pl.borek497.bookstore.order.domain.Delivery.COURIER
import static pl.borek497.bookstore.order.domain.Delivery.SELF_PICKUP

class ParametrizedPriceServiceSpecificationTest extends Specification {

    @Shared
    PriceService priceService

    def setupSpec() {
        priceService = new PriceService()
    }

    def "should apply appropriate delivery cost based on order price"() {

        given:
        Book germinal = new Book("Germinal", 1885, BigDecimal.valueOf(pricePerBook), 50)
        OrderItem germinalItem = new OrderItem(germinal, Integer.valueOf(quantity))
        Order germinalOrder = Order.builder().item(germinalItem).delivery(delivery).build()

        when:
        OrderPrice germinalPrice = priceService.calculatePrice(germinalOrder)

        then:
        germinalPrice.deliveryPrice == deliveryPrice
        germinalPrice.discounts == discounts
        germinalPrice.finalPrice() == expectedPrice

        where:
        pricePerBook | quantity | delivery    || expectedPrice   || deliveryPrice || discounts
        0.01D        | 1        | SELF_PICKUP || valueOf(0.01D)  || ZERO          || ZERO
        99.99        | 1        | COURIER     || valueOf(109.89) || valueOf(9.90) || ZERO
        50           | 1        | COURIER     || valueOf(59.90)  || valueOf(9.90) || ZERO
        50           | 2        | COURIER     || valueOf(100)    || valueOf(9.90) || valueOf(9.90)
    }
}