import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification
import spock.lang.Subject


class TestClass extends Specification {


    def catalogUseCase = Mock(CatalogUseCase)

    @Subject
    def controller = new CatalogController(catalogUseCase)

    def "should return all books with given title"() {
        given:
        // Tworzymy przykładowe książki do testu
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
                "Hobbit i przyjaciele",
                1988,
                BigDecimal.valueOf(59.99),
                20
        )

        // Mockowanie metody findByTitle w CatalogUseCase, aby zwracała listę książek z tytułem "Harry"
        catalogUseCase.findByTitle("Harry") >> [harryAndChamber, harryGoblet]

        // Mockujemy inne metody jako zabezpieczenie, aby upewnić się, że zwracają pustą listę, gdy są wywoływane przypadkowo
        catalogUseCase.findByAuthor(_) >> []
        catalogUseCase.findByTitleAndAuthor(_, _) >> []


        when:


        def books1 = controller.getAll(Optional.of("Harry"), Optional.empty())
        println "Wynik getAll: ${books1}"

        if (books1 == null) {
            println "books1 jest null, sprawdź, dlaczego nie wywołano findByTitle"
        }

        then:
        // Sprawdzamy, czy wynik zawiera dwie książki i jest zgodny z mockowanym wywołaniem
        books1 != null
        books1.size() == 2
        books1.containsAll([harryAndChamber, harryGoblet])
        !books1.contains(hobbit)

        // Weryfikacja, że tylko findByTitle zostało wywołane raz i żadne inne metody nie były wywołane
        1 * catalogUseCase.findByTitle("Harry")
        0 * catalogUseCase.findByAuthor(_)
        0 * catalogUseCase.findByTitleAndAuthor(_, _)
        0 * catalogUseCase.findAll()
    }
}
