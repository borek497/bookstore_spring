import pl.borek497.bookstore.catalog.application.AuthorsService
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository
import pl.borek497.bookstore.catalog.domain.Author
import spock.lang.Specification
import spock.lang.Subject


class AuthorsServiceSpecificationTest extends Specification {

    def repository = Mock(AuthorJpaRepository)

    @Subject
    def service = new AuthorsService(repository)

    def "should return all authors"() {

        given: "create list of authors"
        List<Author> elements = List.of(
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz"))

        repository.findAll() >> elements

        when: "main method from service is called"
        def result = service.findAll()

        then: "it should return list of authors"
        result == elements
    }
}