import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase
import pl.borek497.bookstore.order.domain.Recipient
import pl.borek497.bookstore.order.web.OrderController
import pl.borek497.bookstore.security.UserSecurity
import pl.borek497.bookstore.web.CreatedURI
import spock.lang.Specification

import static pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.*

class OrderServiceSpecificationTest extends Specification {

    def manipulateOrderUseCase = Mock(ManipulateOrderUseCase)
    def queryOrderUseCase = Mock(QueryOrderUseCase)
    def userSecurity = Mock(UserSecurity)

    def controller = new OrderController(manipulateOrderUseCase, queryOrderUseCase, userSecurity)

    def "user can create new order"() {

        given:
        OrderItemCommand orderItemCommand1 = new OrderItemCommand(1L, 2)
        OrderItemCommand orderItemCommand2 = new OrderItemCommand(12L, 5)
        Recipient recipient = new Recipient("arek@op.pl", "Arek", "123456", "Nowa", "00-987", "Wozy")

        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .items(List.of(orderItemCommand1, orderItemCommand2))
                .recipient(recipient)
                .build()

        def expectedOrderId = 123L

        URI expectedURI = new URI("/orders/$expectedOrderId")

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setRequestURI(expectedURI.toString())
        ServletRequestAttributes attributes = new ServletRequestAttributes(request)
        RequestContextHolder.setRequestAttributes(attributes)
        GroovySpy(ServletUriComponentsBuilder, global: true)
        def mockUriComponentsBuilder = Mock(ServletUriComponentsBuilder)
        mockUriComponentsBuilder.path(_) >> mockUriComponentsBuilder
        mockUriComponentsBuilder.build() >> expectedURI

        ServletUriComponentsBuilder.fromCurrentRequestUri() >> mockUriComponentsBuilder
        //CreatedURI createdURI = new CreatedURI("/orders/123")
        //URI uri2 = createdURI.uri()
        //expectedURI.toString() == "http://localhost/some/request/uri/orders/123"
        expectedURI.toString() == "/orders/$expectedOrderId"


        controller.orderUri(expectedOrderId) >> expectedURI

        def eitherResponse = new PlaceOrderResponse(true, null, expectedOrderId)


        when:
        ResponseEntity<Object> response = controller.createOrder(placeOrderCommand)

        then:
        1 * manipulateOrderUseCase.placeOrder(placeOrderCommand) >> eitherResponse
        response.statusCode == HttpStatus.CREATED
        response.headers.location == expectedURI
    }



    def "ServletUriComponentsBuilder mock test"() {//move to other place, just for test
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