import org.spockframework.runtime.SpecificationContext
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Shared
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.COURIER
import static pl.borek497.bookstore.order.domain.Delivery.SELF_PICKUP

class PriceServiceSpecification extends Specification {

    @Shared
    PriceService priceService = new PriceService()

    @Shared
    Book harryChamberOfSecrets

    def setup() {
        if (testNeedSharedObjects()) {
            harryChamberOfSecrets = new Book(
                    "Harry Potter i Komnata tajemnic", 1998, BigDecimal.valueOf(50), 20
            )
        }
    }

    boolean testNeedSharedObjects() {
        def currentTestName = specificationContext.currentIteration.name
        return currentTestName in [
                "should add delivery cost for order lower than 100 PLN",
                "should delivery cost be free for orders equal to or greater than 100 PLN"
        ]
    }


    def "should calculate final price value as 0 for empty order"() {

        given:
        Order emptyOrder = Order.builder().build()

        when:
        OrderPrice emptyOrderPrice = priceService.calculatePrice(emptyOrder)

        then:
        emptyOrder.deliveryPrice == BigDecimal.ZERO
        emptyOrderPrice.finalPrice() == BigDecimal.ZERO
    }

    def "should add delivery cost for order lower than 100 PLN"() {

        given:
        OrderItem orderItem2 = new OrderItem(harryChamberOfSecrets, 1)
        Order order2 = Order.builder().item(orderItem2).delivery(COURIER).build()

        when:
        OrderPrice orderPrice2 = priceService.calculatePrice(order2)

        then:
        orderPrice2.deliveryPrice == BigDecimal.valueOf(9.90)
        orderPrice2.discounts == BigDecimal.ZERO
        orderPrice2.finalPrice() == BigDecimal.valueOf(59.90)
    }

    def "should delivery cost be free for orders equal to or greater than 100 PLN"() {

        given:
        OrderItem orderItem3 = new OrderItem(harryChamberOfSecrets, 2)
        Order order3 = Order.builder().item(orderItem3).delivery(COURIER).build()

        when:
        OrderPrice orderPrice3 = priceService.calculatePrice(order3)

        then:
        orderPrice3.deliveryPrice == BigDecimal.valueOf(9.90)
        orderPrice3.discounts == BigDecimal.valueOf(9.90)
        orderPrice3.finalPrice() == BigDecimal.valueOf(100.00)
    }

    def "should self pickup cost be free for orders equal to or greater than 1 PLN"() {
        given:
        Book informationLeaflet = new Book(
                "Gmina Wetlina - Informator",
                2024,
                BigDecimal.valueOf(1),
                100
        )

        OrderItem leafletItem = new OrderItem(informationLeaflet, 1)
        Order leafletOrder = Order.builder().item(leafletItem).delivery(SELF_PICKUP).build()

        when:
        OrderPrice leafletPrice = priceService.calculatePrice(leafletOrder)

        then:
        leafletPrice.deliveryPrice == BigDecimal.ZERO
        leafletPrice.finalPrice() == BigDecimal.valueOf(1)
    }

    def "should cheapest book in the order be half price for when order price equal to or greater than 200 PLN"() {

        given:
        Order windBooksOrder = Order.builder().items(prepareOrderAbove200Pln()).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windBooksPrice = priceService.calculatePrice(windBooksOrder)

        then:
        windBooksPrice.deliveryPrice == BigDecimal.ZERO
        windBooksPrice.finalPrice() == BigDecimal.valueOf(322.50)
        windBooksPrice.discounts == BigDecimal.valueOf(17.50)
    }

    def "should cheapest one book in the order should be free when order price equal to or grater than 400 PLN"() {

        given:
        Order windBooksOrder = Order.builder().items(prepareOrderAbove400Pln()).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windBooksPrice = priceService.calculatePrice(windBooksOrder)

        then:
        windBooksPrice.deliveryPrice == BigDecimal.ZERO
        windBooksPrice.discounts == BigDecimal.valueOf(80.00)
        windBooksPrice.finalPrice() == BigDecimal.valueOf(480)
    }


    private Set<OrderItem> prepareOrderAbove200Pln() {
        OrderItem southItem = new OrderItem(new Book("Wiatr południa", 1998, BigDecimal.valueOf(50.00), 20), 2)
        OrderItem northItem = new OrderItem(new Book("Wiatr północy", 1998, BigDecimal.valueOf(45.00), 20), 2)
        OrderItem westItem = new OrderItem(new Book("Wiatr zachodu", 1998, BigDecimal.valueOf(40.00), 20), 2)
        OrderItem eastItem = new OrderItem(new Book("Wiatr wschodu", 1998, BigDecimal.valueOf(35.00), 20), 2)
        return Set.of(southItem, northItem, westItem, eastItem)
    }

    private Set<OrderItem> prepareOrderAbove400Pln() {
        OrderItem floodItem = new OrderItem(new Book("Potop", 1866, BigDecimal.valueOf(100.00), 10), 4)
        OrderItem dollItem = new OrderItem(new Book("Lalka", 1889, BigDecimal.valueOf(80.00), 5), 2)
        return Set.of(floodItem, dollItem)
    }
}