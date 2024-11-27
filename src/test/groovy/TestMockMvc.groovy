import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.catalog.web.CatalogController
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

//@WebMvcTest(CatalogController)
@SpringBootTest
@AutoConfigureMockMvc
class TestMockMvc extends Specification {

    @Autowired
    MockMvc mockMvc

    def catalogUseCase = Mock(CatalogUseCase)

    def "should correctly add new book"() {
        given: "Przygotowanie danych wejściowych i mockowanie serwisu"
        assert mockMvc != null

        def becomeJavaDevBook = new Book("Become Java dev", 2024, BigDecimal.valueOf(89.99), 1)
        becomeJavaDevBook.setId(1L)

        def becomeJavaDevRest = new CatalogController.RestBookCommand(
                becomeJavaDevBook.getTitle(),
                Set.of(1L, 2L),
                becomeJavaDevBook.getYear(),
                becomeJavaDevBook.getAvailable(),
                becomeJavaDevBook.getPrice()
        )

        catalogUseCase.addBook(_) >> becomeJavaDevBook

        when: "Wykonujemy symulowane żądanie HTTP POST"
        def response = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(becomeJavaDevRest)))

        then: "Oczekujemy poprawnego wyniku"
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost:8080/books/1"))
    }

    def "should respond with 200 OK"() {
        when:
        def response = mockMvc.perform(get("/test"))

        then:
        response.andExpect(status().isOk())
    }

}