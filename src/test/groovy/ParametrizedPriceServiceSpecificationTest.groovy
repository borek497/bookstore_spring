import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Shared
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.COURIER


class ParametrizedPriceServiceSpecificationTest extends Specification {

    @Shared
    PriceService priceService

    Book harryChamberOfSecrets

    def setup() {
        //this solution will create new, fresh object for each test but only for those in method: testNeeded
        //don't use @Shared with setup()
        if (testNeedHarryBook()) {
            harryChamberOfSecrets = new Book(
                    "Harry Potter i Komnata tajemnic", 1998, BigDecimal.valueOf(50), 20
            )
        }
    }

    def setupSpec() {
        priceService = new PriceService()
    }

    boolean testNeedHarryBook() {
        def currentTestName = specificationContext.currentIteration.name
        return currentTestName in [
                "should add delivery cost for order lower than 100 PLN",
                "should not apply delivery cost for orders of 100 PLN or more"
        ]
    }

    def "should apply appropriate delivery cost based on order price"() {

        given:
        Book germinal = new Book("Germinal", 1885, BigDecimal.valueOf(pricePerBook), 50)
        OrderItem germinalItem = new OrderItem(germinal, Integer.valueOf(quantity))
        Order germinalOrder = Order.builder().item(germinalItem).delivery(COURIER).build()

        when:
        OrderPrice germinalPrice = priceService.calculatePrice(germinalOrder)

        then:
        germinalPrice.deliveryPrice == deliveryPrice
        germinalPrice.discounts == discounts
        germinalPrice.finalPrice() == expectedPrice

        where:
        pricePerBook | quantity || expectedPrice             || deliveryPrice            || discounts
        0            | 0        || BigDecimal.ZERO           || BigDecimal.valueOf(9.90) || BigDecimal.ZERO
        50           | 1        || BigDecimal.valueOf(59.90) || BigDecimal.valueOf(9.90) || BigDecimal.ZERO
        50           | 2        || BigDecimal.valueOf(100)   || BigDecimal.valueOf(9.90) || BigDecimal.valueOf(9.90)
    }
}