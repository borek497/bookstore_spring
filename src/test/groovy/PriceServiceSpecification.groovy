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
        Order emptyOrder = Order.builder().build();

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

    def "should delivery cost be free for orders equal to or above 100 PLN"() {

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

    def "should self pickup cost be free for orders equal to or above 1 PLN"() {

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

    def "should cheapest book in the order be half price"() {

        given:
        Book southWind = new Book(
                "Wiatr południa",
                1998,
                BigDecimal.valueOf(50.00),
                20
        )

        Book northWind = new Book(
                "Wiatr północy",
                1998,
                BigDecimal.valueOf(45.00),
                20
        )

        Book westWind = new Book(
                "Wiatr zachodu",
                1998,
                BigDecimal.valueOf(40.00),
                20
        )

        Book eastWind = new Book(
                "Wiatr wschodu",
                1998,
                BigDecimal.valueOf(35.00),
                20
        )
        OrderItem southItem = new OrderItem(southWind, 2)
        OrderItem northItem = new OrderItem(northWind, 2)
        OrderItem westItem = new OrderItem(westWind, 2)
        OrderItem eastItem = new OrderItem(eastWind, 2)
        Set<OrderItem> o = Set.of(southItem, northItem, westItem, eastItem);
        //Order w = new Order(o, SELF_PICKUP);
        Order winds = Order.builder().item(o).delivery(SELF_PICKUP).build()

        when:
        OrderPrice windsPrice = priceService.calculatePrice(winds)

        then:
        windsPrice.finalPrice() == BigDecimal.valueOf(305.00)
        OrderItem o1 = winds.items.stream()
                .filter(x -> x.book.getTitle().equals("Wiatr wschodu"))
                .findFirst()
        o1.book.getPrice() == BigDecimal(17.50)
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