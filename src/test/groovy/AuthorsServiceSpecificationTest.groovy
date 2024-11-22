import pl.borek497.bookstore.catalog.application.AuthorsService
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository
import pl.borek497.bookstore.catalog.domain.Author
import spock.lang.Specification
import spock.lang.Subject

import static java.lang.System.out


class AuthorsServiceSpecificationTest extends Specification {

    def repository = Mock(AuthorJpaRepository)

    @Subject
    def service = new AuthorsService(repository)

    def "should return all authors"() {

        given: "create list of authors"
        def authors = List.of(
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz"))

        repository.findAll() >> authors

        when: "main method from service is called"
        def result = service.findAll()

        then: "should return list of authors"
        result == authors
    }

    def "should return all authors from repository with debug"() {

        given: "create list of authors"
        def authors = [
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz")
        ]
        out.println("Authors list initialized: $authors")

        repository.findAll() >> {
            out.println("Mock method 'findAll()' called")
            return authors
        }

        when: "main method from service is called"
        def result = service.findAll()
        out.println("Result from service.findAll(): $result")

        then: "should return list of authors"

        out.println("Expected: $authors")
        out.println("Actual: $result")
        assert service.authorJpaRepository == repository
        out.println("Service is using mocked repository: ${service.authorJpaRepository == repository}")
        result == authors
    }
}