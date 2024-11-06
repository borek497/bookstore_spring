import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification
import spock.lang.Subject

import static java.math.BigDecimal.valueOf


class CatalogControllerSpecificationTest extends Specification {

    def catalogUseCase = Mock(CatalogUseCase)

    @Subject
    def controller = new CatalogController(catalogUseCase)

    Book harryAndChamber
    Book harryGoblet
    Book hobbit
    Long bookId

    def setup() {
        setupBooks()
    }

    private void setupBooks() {
        harryAndChamber = new Book("Harry Potter i komnata tajemnic", 1998, valueOf(49.99), 20)
        harryGoblet = new Book("Harry Potter i czara ognia", 1999, valueOf(49.99), 20)
        hobbit = new Book("Hobbit i przyjaciele", 1988, valueOf(59.99), 20)
        bookId = 1L
    }

    def "should return book when given book id exists"() {

        when:
        def responseEntity = controller.getById(bookId)

        then:
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body == harryAndChamber
        1 * catalogUseCase.findById(bookId) >> Optional.of(harryAndChamber)
    }

    def "should return 404 when book with given id does not exist"() {

        when:
        ResponseEntity<Book> response = controller.getById(bookId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == null
        1 * catalogUseCase.findById(bookId) >> Optional.empty()
    }

    def "should return all books"() {

        when:
        def books = controller.getAll(Optional.empty(), Optional.empty())

        then:
        books.size() == 3
        books.containsAll([harryAndChamber, harryGoblet, hobbit])
        1 * catalogUseCase.findAll() >> [harryAndChamber, harryGoblet, hobbit]
    }

    def "should return all books with given title"() {

        when:
        List<Book> books = controller.getAll(Optional.of("Harry"), Optional.empty())

        then:
        1 * catalogUseCase.findByTitle("Harry") >> [harryGoblet, harryAndChamber]
        0 * catalogUseCase.findByAuthor(_)
        0 * catalogUseCase.findByTitleAndAuthor(_, _)
        0 * catalogUseCase.findAll()
        books.size() == 2
        books.containsAll([harryAndChamber, harryGoblet])
        !books.contains(hobbit)
    }
}