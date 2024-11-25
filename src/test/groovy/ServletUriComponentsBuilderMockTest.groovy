import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.borek497.bookstore.web.CreatedURI
import spock.lang.Specification


/**
 * Klasa wykorzystuje GroovySpy.
 * To narzędzie w Spock, które pozwala na mokowanie/stubowanie obiektów i klas statycznych w Groovy.
 * Wyjaśnienie w notatkach
 */
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

    def "should add Location header with correct URL in response"() {
        given: "A mocked HTTP request and response"
        def request = new MockHttpServletRequest("POST", "/orders")
        request.serverName = "localhost"
        request.serverPort = 8080
        request.scheme = "http"

        // Ustawienie request jako bieżący kontekst
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        def response = new MockHttpServletResponse()

        when: "Generating Location URL using ServletUriComponentsBuilder"
        def orderId = "602"
        def locationUrl = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(orderId)
                .toUriString()

        // Dodanie nagłówka Location do odpowiedzi
        response.setHeader("Location", locationUrl)

        then: "The Location header should have the correct URL"
        response.getHeader("Location") == "http://localhost:8080/orders/602"
    }
}

