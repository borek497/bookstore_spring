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
        def authors = List.of(
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz"))

        repository.findAll() >> authors

        when: "main method from service is called"
        def result = service.findAll()

        then: "it should return list of authors"
        //1 * repository.findAll()
        result == authors
    }

    def "should return all authors from repository with debug"() {//pass
        given: "a list of authors in the repository"
        def authors = [
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz")
        ]
        repository.findAll() >> {
            println("Mock method 'findAll()' called")
            return authors
        }

        when: "findAll is called"
        def result = service.findAll()
        println("Result from service.findAll(): $result")

        then: "the repository's findAll is called once"
        1 * repository.findAll() >> authors

        and: "the service returns the same list of authors"
        println("Expected: $authors")
        println("Actual: $result")
        assert service.authorJpaRepository == repository
        println("Service is using mocked repository: ${service.authorJpaRepository == repository}")

        result == authors

    }

    def "should return all authors from repository22"() {
        given: "a list of authors in the repository"
        def authors = [
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz")
        ]
        repository.findAll() >> authors // Definiujemy zachowanie mocka

        when: "findAll is called"
        def result = service.findAll()

        then: "the repository's findAll is called once"
        1 * repository.findAll() // Sprawdzamy, że mock był wywołany

        and: "the service returns the same list of authors"
        result == authors
    }

    def "should return all authors from repository with debug33"() {

        given:
        def authors = [
                new Author("Anna"),
                new Author("Marcelina"),
                new Author("Mateusz")
        ]
        println("Authors list initialized: $authors")

        repository.findAll() >> {
            println("Mock repository.findAll() called")
            return authors
        }
        println("Mock repository.findAll() configured to return: $authors")

        when:
        println("Calling service.findAll()")
        def result = service.findAll()
        println("Result from service.findAll(): $result")

        then:
        println("Expected: $authors")
        println("Actual: $result")
        result == authors
    }



}