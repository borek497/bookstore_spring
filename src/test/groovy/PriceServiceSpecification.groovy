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
    PriceService priceService

    Book harryChamberOfSecrets

    def setup() {
        createBookIfNull();
    }

    def setupSpec() {
        priceService = new PriceService()
    }

    void createBookIfNull() {
        if (harryChamberOfSecrets == null) {
            harryChamberOfSecrets = new Book(
                    "Harry Potter i Komnata tajemnic", 1998, BigDecimal.valueOf(50), 20)
        }
    }

    //Not PriceServiceTest
    def "should throw exception for empty order"() {

        given:
        Order emptyOrder = Order.builder().build()

        when:
        emptyOrder.validateItemsNotEmpty()

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Order with 0 or negative quantity is prohibited"
    }

    //Not PriceServiceTest
    def "should throw exception when 0 or negative quantity of book"() {

        given:
        OrderItem harryZeroQuantityItem = new OrderItem(harryChamberOfSecrets, 0)
        Order harryZeroQuantityOrder = Order.builder().item(harryZeroQuantityItem).delivery(COURIER).build()

        when:
        OrderPrice harryZeroQuantityPrice = priceService.calculatePrice(harryZeroQuantityOrder)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Order with 0 or negative quantity is prohibited"
        harryZeroQuantityPrice == null
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

    def "should not apply delivery cost for orders of 100 PLN or more"() {

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

    def "should apply free self-pickup for orders over 1 PLN"() {
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

    def "should apply half-price discount to cheapest book on 200+ PLN orders"() {

        given:
        Order windBooksOrder = Order.builder().items(prepareOrderAbove200Pln()).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windBooksPrice = priceService.calculatePrice(windBooksOrder)

        then:
        windBooksPrice.deliveryPrice == BigDecimal.ZERO
        windBooksPrice.finalPrice() == BigDecimal.valueOf(322.50)
        windBooksPrice.discounts == BigDecimal.valueOf(17.50)
    }

    def "should give cheapest book for free on 400+ PLN orders"() {

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
        def southItem = new OrderItem(new Book("Wiatr południa", 1998, 50.00G, 20), 2)
        def northItem = new OrderItem(new Book("Wiatr północy", 1998, 45.00G, 20), 2)
        def westItem = new OrderItem(new Book("Wiatr zachodu", 1998, 40.00G, 20), 2)
        def eastItem = new OrderItem(new Book("Wiatr wschodu", 1998, 35.00G, 20), 2)
        [southItem, northItem, westItem, eastItem] as Set
    }

    private Set<OrderItem> prepareOrderAbove400Pln() {
        def floodItem = new OrderItem(new Book("Potop", 1866, 100.00G, 10), 4)
        def dollItem = new OrderItem(new Book("Lalka", 1889, 80.00G, 5), 2)
        [floodItem, dollItem] as Set
    }
}