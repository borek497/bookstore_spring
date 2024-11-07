import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.borek497.bookstore.web.CreatedURI
import spock.lang.Specification


class ServletUriComponentsBuilderMockTest extends Specification {

    def "ServletUriComponentsBuilder mock test"() {
        given:
        String uri = "http://localhost/some/request/uri/orders/123"

        URI expectedUri = new URI(uri)


        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setRequestURI(uri)

        // Ustawiamy kontekst żądania, by ServletUriComponentsBuilder mogło z niego skorzystać
        ServletRequestAttributes attributes = new ServletRequestAttributes(request)
        RequestContextHolder.setRequestAttributes(attributes)

        // Tworzymy GroovySpy dla ServletUriComponentsBuilder
        GroovySpy(ServletUriComponentsBuilder, global: true)

        // Mockujemy statyczną metodę fromCurrentRequestUri(), aby zwróciła builder
        def mockUriComponentsBuilder = Mock(ServletUriComponentsBuilder)
        mockUriComponentsBuilder.path(_) >> mockUriComponentsBuilder
        mockUriComponentsBuilder.build() >> expectedUri

        ServletUriComponentsBuilder.fromCurrentRequestUri() >> mockUriComponentsBuilder

        // Tworzymy instancję CreatedURI
        CreatedURI createdURI = new CreatedURI("/orders/123")

        when:
        // Wywołanie metody uri() na CreatedURI
        URI uri2 = createdURI.uri()

        then:
        // Sprawdzamy, czy zwrócone URI jest zgodne z oczekiwanym
        uri2.toString() == "http://localhost/some/request/uri/orders/123"
    }
}