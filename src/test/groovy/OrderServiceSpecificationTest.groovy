import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase
import pl.borek497.bookstore.order.domain.Recipient
import pl.borek497.bookstore.order.web.OrderController
import pl.borek497.bookstore.security.UserSecurity
import spock.lang.Specification

import static pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.*

class OrderServiceSpecificationTest extends Specification {

    def manipulateOrderUseCase = Mock(ManipulateOrderUseCase)
    def queryOrderUseCase = Mock(QueryOrderUseCase)
    def userSecurity = Mock(UserSecurity)

    def controller = new OrderController(manipulateOrderUseCase, queryOrderUseCase, userSecurity)

    def "user can create new order - fix"() {

        given:
        Recipient recipient = new Recipient("arek@op.pl", "Arek", "123456", "Nowa", "00-987", "Wozy")

        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .items(List.of(
                        new OrderItemCommand(1L, 2),
                        new OrderItemCommand(12L, 5))
                )
                .recipient(recipient)
                .build()

        def expectedOrderId = 123L
        def requestMock = new MockHttpServletRequest("POST", "/orders")
        requestMock.setServerName("localhost")
        requestMock.setServerPort(8080)
        requestMock.setScheme("http")

        def attributes = new ServletRequestAttributes(requestMock)
        RequestContextHolder.setRequestAttributes(attributes)

        def eitherResponse = new PlaceOrderResponse(true, null, expectedOrderId)

        when:
        ResponseEntity<Object> response = controller.createOrder(placeOrderCommand)

        then:
        1 * manipulateOrderUseCase.placeOrder(placeOrderCommand) >> eitherResponse
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "http://localhost:8080/orders/${expectedOrderId}"

        cleanup:
        RequestContextHolder.resetRequestAttributes()
    }

//    def "user can create new order"() {
//
//        given:
//        OrderItemCommand orderItemCommand1 = new OrderItemCommand(1L, 2)
//        OrderItemCommand orderItemCommand2 = new OrderItemCommand(12L, 5)
//        Recipient recipient = new Recipient("arek@op.pl", "Arek", "123456", "Nowa", "00-987", "Wozy")
//
//        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
//                .items(List.of(orderItemCommand1, orderItemCommand2))
//                .recipient(recipient)
//                .build()
//
//        def expectedOrderId = 123L
//
//        URI expectedURI = new URI("orders")
//
//        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/orders")
//        request.setServerName("localhost")
//        request.setServerPort(8080)
//        request.setScheme("http")
//
//        //request.setRequestURI(expectedURI.toString())
//        //request.setRequestURI("http://localhost/orders/$expectedOrderId")
//        //now ServletRequestAttributes attributes = new ServletRequestAttributes(request)
//        //nowRequestContextHolder.setRequestAttributes(attributes)
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        GroovySpy(ServletUriComponentsBuilder, global: true)
//        def mockUriComponentsBuilder = Mock(ServletUriComponentsBuilder)
////        mockUriComponentsBuilder.path(_) >> mockUriComponentsBuilder
////        mockUriComponentsBuilder.build() >> expectedURI.toString()
//
//        ServletUriComponentsBuilder.fromCurrentRequestUri().path("/") >> mockUriComponentsBuilder
////        CreatedURI createdURI = new CreatedURI("/orders/123")
////        URI uri2 = createdURI.uri()
////        expectedURI.toString() == "http://localhost/some/request/uri/orders/123"
////        expectedURI.toString() == "/orders/$expectedOrderId"
//
//
//        def eitherResponse = new PlaceOrderResponse(true, null, expectedOrderId)
//        //controller.orderUri(expectedOrderId) >> expectedURI.toString()
//
//        when:
//        //nowResponseEntity<Object> response = controller.createOrder(placeOrderCommand)
//
//        then:
//        1 * manipulateOrderUseCase.placeOrder(placeOrderCommand) >> eitherResponse
//        //controller.orderUri(expectedOrderId) >> expectedURI.toString()
//        response.statusCode == HttpStatus.CREATED
//        //response.headers.location == expectedURI.toString()
//        //response.headers.location == new URI("http://localhost/orders/$expectedOrderId")
//        response.headers.location == expectedURI
//    }
}