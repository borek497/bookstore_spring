import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.*


class CatalogControllerSpecificationTest extends Specification {

    def catalogUseCase = Mock(CatalogUseCase)

    @Subject
    def controller = new CatalogController(catalogUseCase)

    def "should return book when given book id exists"() {

        given:
        Long bookId = 1L
        Book book = new Book("Harry", 1998, BigDecimal.valueOf(50), 20)

        catalogUseCase.findById(bookId) >> Optional.of(book)

        when:
        def responseEntity = controller.getById(bookId)

        then:
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body == book
    }

    def "should return 404 when book with given id does not exist"() {
        given:
        Long bookId = 1L
        catalogUseCase.findById(bookId) >> Optional.empty()

        when:
        ResponseEntity<Book> response = controller.getById(bookId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == null
    }

    def "should return all books"() {

        given:
        Book book1 = new Book("Harry", 1998, BigDecimal.valueOf(50), 20)
        Book book2 = new Book("Harry", 1998, BigDecimal.valueOf(50), 20)
        Book book3 = new Book("Harry", 1998, BigDecimal.valueOf(50), 20)
        catalogUseCase.findAll() >> [book1, book2, book3]

        when:
        def books = controller.getAll(Optional.empty(), Optional.empty())

        then:
        books.size() == 3
        books.containsAll([book1, book2, book3])
    }

    def "should return all books with given title"() {

        given:
        Book harryAndChamber = new Book(
                "Harry Potter i komnata tajemnic",
                1998,
                BigDecimal.valueOf(49.99),
                20
        )

        Book harryGoblet = new Book(
                "Harry Potter i czara ognia",
                1999,
                BigDecimal.valueOf(49.99),
                20
        )

        Book hobbit = new Book(
                "Hobbit i przyajciele",
                1988,
                BigDecimal.valueOf(59.99),
                20
        )

        catalogUseCase.findByTitle("Harry") >> List.of(harryGoblet, harryAndChamber)
        //catalogUseCase.findByTitle("Harry") >> new ArrayList<Book>([harryGoblet, harryAndChamber])
//        List<Book> books = new ArrayList<>()
//        books.add(harryAndChamber)
//        books.add(harryGoblet)
//        catalogUseCase.findByTitle("Harry") >> books
        //println "wynik dla findByTitle ${catalogUseCase.findByTitle("Harry") >> [harryGoblet, harryAndChamber]}"

        when:
        List<Book> el = controller.getAll(Optional.of("Harry"), Optional.empty())

        then:
        el.size() == 2
        //el.containsAll([harryAndChamber, harryGoblet])
        //!el.contains(hobbit)
        1 * catalogUseCase.findByTitle("Harry")
        0 * catalogUseCase.findByAuthor(_)
        0 * catalogUseCase.findByTitleAndAuthor(_, _)
        0 * catalogUseCase.findAll()
    }




//    //Potrzebujemy mock
//    def "ksiażka o zbyt niskim nakładzie (available >= 0)"() {
//
//        given:'
//        Book book = new Book(
//                "Harry Potter i Komnata tajemnic", 1998, BigDecimal.valueOf(50), 0)
//        OrderItem book2 = new OrderItem(book, 1)
//        Order order2 = Order.builder().item(book2).delivery(COURIER).build()
//
//        when:
//        OrderPrice orderPrice2 = priceService.calculatePrice(order2)
//
//        then:
//        orderPrice2.deliveryPrice == BigDecimal.valueOf(9.90)
//        orderPrice2.discounts == BigDecimal.ZERO
//        orderPrice2.finalPrice() == BigDecimal.valueOf(59.90)
//    }
}