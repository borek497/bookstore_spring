//import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
//import pl.borek497.bookstore.catalog.domain.Book
//import pl.borek497.bookstore.catalog.web.CatalogController
//import spock.lang.Specification
//
//class TestClass extends Specification {
//
//    // Używamy natywnego mechanizmu Spocka do tworzenia mocka
//    CatalogUseCase catalogUseCase = Mock()
//
//    // Tworzymy ręcznie kontroler z wstrzykniętym mockiem
//    CatalogController controller = new CatalogController(catalogUseCase)
//
//    def "should return all books"() {
//        given: "Catalog contains two books"
//        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L)
//        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L)
//
//        // Definiujemy zachowanie mocka: gdy zostanie wywołana metoda findAll, zwróci listę książek
//        catalogUseCase.findAll() >> [effectiveJava, concurrency]
//
//        when: "Client requests all books"
//        def allBooks = controller.getAll(Optional.empty(), Optional.empty())
//
//        then: "The response contains two books"
//        allBooks.size() == 2
//    }
//}
