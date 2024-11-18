import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.borek497.bookstore.web.CreatedURI
import spock.lang.Specification


class UriCreationSpecificationTest extends Specification {

    def "should create URI correctly"() {

        given:
        def id = 1
        def createdUri = new CreatedURI("/path/$id")

        def requestMock = new MockHttpServletRequest("POST", "/books")
        requestMock.setServerName("localhost")
        requestMock.setServerPort(8080)
        requestMock.setScheme("http")

        def attributes = new ServletRequestAttributes(requestMock)
        RequestContextHolder.setRequestAttributes(attributes)

        when:
        URI result = createdUri.uri()

        then:
        result.toString() == "http://localhost:8080/books/path/$id"

        cleanup:
        RequestContextHolder.resetRequestAttributes()
    }

    def "should throw IllegalStateException if no request context is set"() {
        given:
        def createdURI = new CreatedURI("/new/path")

        when:
        createdURI.uri()

        then:
        thrown(IllegalStateException)
    }
}