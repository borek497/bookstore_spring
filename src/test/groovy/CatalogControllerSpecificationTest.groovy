import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Author
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification
import spock.lang.Subject

import static java.math.BigDecimal.valueOf
import static pl.borek497.bookstore.catalog.web.CatalogController.RestBookCommand

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

    def "should return empty list when given title does not exist"() {

        when:
        List<Book> books = controller.getAll(Optional.of("Unknown title"), Optional.empty())

        then:
        1 * catalogUseCase.findByTitle("Unknown title") >> []
        books.size() == 0
    }

    def "should return all books with given author"() {

        given:
        Author henry = new Author("Henryk Sienkiewicz")
        henry.setId(bookId)

        Book deluge = new Book("Potop", 1877, new BigDecimal("89.99"), 20)

        when:
        def books = controller.getAll(Optional.empty(), Optional.of("Sienkiewicz"))

        then:
        1 * catalogUseCase.findByAuthor("Sienkiewicz") >> [deluge]
        books.size() == 1
        books.contains(deluge)
        books.every { it == deluge } //it == deluge comparing every element with given object -> deluge
    }

    def "should return empty list when given author does not exist"() {

        when:
        def books = controller.getAll(Optional.empty(), Optional.of("Unknown author"))

        then:
        1 * catalogUseCase.findByAuthor("Unknown author") >> []
        books.size() == 0
    }

    def "should correctly add new book"() {

        given:
        Book becomeJavaDevBook = new Book("Become Java dev",2024, valueOf(89.99), 1)
        becomeJavaDevBook.setId(1L)

        RestBookCommand becomeJavaDevRest = new RestBookCommand(
            becomeJavaDevBook.getTitle(),
                Set.of(1L, 2L),
                becomeJavaDevBook.getYear(),
                becomeJavaDevBook.getAvailable(),
                becomeJavaDevBook.getPrice()
        )

        def requestMock = new MockHttpServletRequest("POST", "/books")
        requestMock.setServerName("localhost")
        requestMock.setServerPort(8080)
        requestMock.setScheme("http")

        def attributes = new ServletRequestAttributes(requestMock)
        RequestContextHolder.setRequestAttributes(attributes)

        when:
        ResponseEntity<Void> response = controller.addBook(becomeJavaDevRest)

        then:
        1 * catalogUseCase.addBook(_) >> becomeJavaDevBook
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "http://localhost:8080/books/1"

        cleanup:
        RequestContextHolder.resetRequestAttributes()
    }
}