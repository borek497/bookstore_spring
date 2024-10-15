import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerApiTest2 extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    TestRestTemplate restTemplate

//    @Autowired
//    CatalogController controller
//
//    CatalogUseCase catalogUseCase = Mock()  // Tworzymy mock dla zależności

    def "should return all books"() {
        given: "Catalog contains two books"
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.90"), 50L)
        Book concurrency = new Book("Java Concurrency", 2006, new BigDecimal("99.90"), 50L)

        catalogUseCase.findAll() >> [effective, concurrency]

        // Tworzymy instancję kontrolera ręcznie, zamiast korzystać z pełnego kontekstu Springa
        def controller = new CatalogController(catalogUseCase)

        when: "Client requests all books from catalog"
        def books = controller.getAll(Optional.empty(), Optional.empty())

        then: "The response contains two books"
        books.size() == 2
    }

    def setup() {
        controller.catalogUseCase = catalogUseCase  // Wstrzykujemy mock do kontrolera
    }

    def "should return all books2"() {
        given: "Catalog contains two books"
        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L)
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L)

        catalogUseCase.findAll() >> [effectiveJava, concurrency]  // Definiujemy zachowanie mocka

        when: "Client requests all books"
        def allBooks = controller.getAll(Optional.empty(), Optional.empty())

        then: "The response contains two books"
        allBooks.size() == 2
    }

    @Autowired
    CatalogController controller  // Spring automatycznie wstrzyknie kontroler

    @MockBean
    CatalogUseCase catalogUseCase  // Spring automatycznie wstrzyknie mock

    def "should return all books3"() {
        given: "Catalog contains two books"
        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L)
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L)

        catalogUseCase.findAll() >> [effectiveJava, concurrency]  // Definiujemy zachowanie mocka

        when: "Client requests all books"
        def allBooks = controller.getAll(Optional.empty(), Optional.empty())

        then: "The response contains two books"
        allBooks.size() == 2
    }
}