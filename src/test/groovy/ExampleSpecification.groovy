import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.db.AuthorJpaRepository
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import pl.borek497.bookstore.demo.Cities
import pl.borek497.bookstore.demo.HorsesFarm
import pl.borek497.bookstore.demo.TooSmallHorsesException
import spock.lang.Specification

/**
 * Spock tests are called Specifications
 * Don't using any annotations like @Test from JUnit
 */

class ExampleSpecification extends Specification {

//    CatalogUseCase catalogUseCase = Mock()
//
//    CatalogController catalogController = Mock()

    def "Should be a simple assertion"() {
        expect:
        1 == 1
    }

    def "Should demonstrate given-when-then"() {
        when:
        int horses = new HorsesFarm(10).getNumberOfHorses()

        then:
        horses == 10;
    }

    def "Should expect exception"() {
        when:
        new HorsesFarm(0)

        then:
        thrown(TooSmallHorsesException)
    }

    def "Should be able to create horse farm with #horses horses"() {
        expect:
        new HorsesFarm(horses).numberOfHorses == horses;

        where:
        horses << [11, 16, 80, 10000]
    }

    def "Should use data tables for calculating max. Max of #a and #b is #max"() {
        expect:
        Math.max(a, b) == max;

        where:
        a | b || max
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }

    def "Should be able to mock a concrete class"() {
        given:
        //two ways of declare mock
//        UserService userService = Mock();
//        def userService2 = Mock(UserService)

        givenEffectiveJava();
        givenJavaConcurencyInPractice();

        when:
        List<Book> all = catalogController.getAll(Optional.empty(), Optional.empty());

        then:
        all.size() == 2;




    }

    def "should get all books"() {
        given: "two books returned by the use case"
        def effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L)
        def concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L)
        catalogUseCase.findAll() >> [effectiveJava, concurrency]

        when: "requesting all books from the controller"
        def allBooks = catalogController.getAll(Optional.empty(), Optional.empty())

        then: "we should get two books"
        allBooks.size() == 2
    }

    def "should get correct city data"() {
        given:
        def cities = new LinkedList<Cities>()

        when:
        cities.add(new Cities("Warsaw", 10000))
        cities.add(new Cities("Cracow", 2000))
        cities.add(new Cities("Dublin", 100000))

        then:
        cities*.getCityName() == ["Warsaw", "Cracow", "Dublin"]

    }
}