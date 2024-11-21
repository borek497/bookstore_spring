import org.springframework.http.HttpStatus
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification
import spock.lang.Subject

import static java.math.BigDecimal.valueOf


class Test extends Specification {

    def catalogUseCase = Mock(CatalogUseCase)

    @Subject
    def controller = new CatalogController(catalogUseCase)

    def "should return book when given book id exists"() {

        given:
        long bookId = 1L
        Book harryAndChamber = new Book("Harry Potter i komnata tajemnic", 1998, valueOf(49.99), 20)

        catalogUseCase.findById(bookId) >> Optional.of(harryAndChamber)

        when:
        def responseEntity = controller.getById(bookId)

        then:
        1 * catalogUseCase.findById(bookId)
        and:
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.body == harryAndChamber

    }
}