import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.price.OrderPrice
import pl.borek497.bookstore.order.application.price.PriceService
import pl.borek497.bookstore.order.domain.Order
import pl.borek497.bookstore.order.domain.OrderItem
import spock.lang.Specification

import static pl.borek497.bookstore.order.domain.Delivery.COURIER
import static pl.borek497.bookstore.order.domain.Delivery.SELF_PICKUP

class PriceServiceSpecification extends Specification {

    PriceService priceService = new PriceService()

    def "should calculate final price value as 0 for empty order"() {

        given:
        Order emptyOrder = Order.builder().build()

        when:
        OrderPrice emptyOrderPrice = priceService.calculatePrice(emptyOrder)

        then:
        emptyOrderPrice.finalPrice() == BigDecimal.ZERO
    }

    def "should add delivery cost for order lower than 100 PLN"() {

        given:
        Book harryChamberOfSecrets = new Book(
                "Harry Potter i Komnata tajemnic",
                1998,
                BigDecimal.valueOf(50),
                20
        )

        OrderItem harryChamberItem = new OrderItem(harryChamberOfSecrets, 1)
        Order harryChamberOrder = Order.builder().item(harryChamberItem).delivery(COURIER).build()

        when:
        OrderPrice harryChamberPrice = priceService.calculatePrice(harryChamberOrder)

        then:
        harryChamberPrice.finalPrice() == BigDecimal.valueOf(59.90)
    }

    def "should delivery cost be free for orders equal to or greater than 100 PLN"() {

        given:
        Book harryChamberOfSecrets = new Book(
                "Harry Potter i Komnata tajemnic",
                1998,
                BigDecimal.valueOf(50),
                20
        )

        OrderItem harryChamberItem = new OrderItem(harryChamberOfSecrets, 2)
        Order harryChamberOrder = Order.builder().item(harryChamberItem).delivery(COURIER).build()

        when:
        OrderPrice harryChamberPrice = priceService.calculatePrice(harryChamberOrder)

        then:
        harryChamberPrice.finalPrice() == BigDecimal.valueOf(100)
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
        leafletPrice.finalPrice() == BigDecimal.valueOf(1)
    }

    def "should cheapest book in the order be half price for when order price equal to or greater than 200 PLN"() {

        given:
        Order windBooksOrder = Order.builder().items(prepareOrderAbove200Pln()).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windBooksPrice = priceService.calculatePrice(windBooksOrder)

        then:
        windBooksPrice.finalPrice() == BigDecimal.valueOf(322.50)
        priceService.discounts(windBooksOrder) == BigDecimal.valueOf(17.50)
    }

    def "should cheapest one book in the order should be free when order price equal to or grater than 400 PLN"() {
        given:
        Order windBooksOrder = Order.builder().items(prepareOrderAbove400Pln()).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windBooksPrice = priceService.calculatePrice(windBooksOrder)

        then:
        windBooksPrice.finalPrice() == BigDecimal.valueOf(480)
    }


    private Set<OrderItem> prepareOrderAbove200Pln() {
        OrderItem southItem = new OrderItem(new Book("Wiatr południa", 1998, BigDecimal.valueOf(50.00), 20), 2)
        OrderItem northItem = new OrderItem(new Book("Wiatr północy", 1998, BigDecimal.valueOf(45.00), 20), 2)
        OrderItem westItem = new OrderItem(new Book("Wiatr zachodu", 1998, BigDecimal.valueOf(40.00), 20), 2)
        OrderItem eastItem = new OrderItem(new Book("Wiatr wschodu", 1998, BigDecimal.valueOf(35.00),20), 2)
        return Set.of(southItem, northItem, westItem, eastItem)
    }

    private Set<OrderItem> prepareOrderAbove400Pln() {
        OrderItem floodItem = new OrderItem(new Book("Potop", 1866, BigDecimal.valueOf(100.00), 10), 4)
        OrderItem dollItem = new OrderItem(new Book("Lalka", 1889, BigDecimal.valueOf(80.00), 5), 2)
        return Set.of(floodItem, dollItem)
    }
}