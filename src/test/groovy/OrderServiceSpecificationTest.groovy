import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.borek497.bookstore.catalog.domain.Book
import pl.borek497.bookstore.order.application.ManipulateOrderService
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase
import pl.borek497.bookstore.order.domain.Recipient
import pl.borek497.bookstore.order.web.OrderController
import pl.borek497.bookstore.security.UserSecurity
import spock.lang.Specification

import static pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.*

class OrderServiceSpecificationTest extends Specification {

    //ToDo - development in progress!!!

    def manipulateOrderUseCase = Mock(ManipulateOrderUseCase)
    def queryOrderUseCase = Mock(QueryOrderUseCase)
    def userSecurity = Mock(UserSecurity)

    Recipient recipient

    def controller = new OrderController(manipulateOrderUseCase, queryOrderUseCase, userSecurity)

    def setup() {
        recipient = new Recipient("arek@op.pl", "Arek", "123456", "Nowa", "00-987", "Wozy")
    }


    def "user can create new order"() {

        given:
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
        manipulateOrderUseCase.placeOrder(placeOrderCommand) >> eitherResponse

        when:
        ResponseEntity<Object> response = controller.createOrder(placeOrderCommand)

        then:
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "http://localhost:8080/orders/${expectedOrderId}"

        cleanup:
        RequestContextHolder.resetRequestAttributes()
    }

    def "user cannot order more books than available"() {

        given:
        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("199.90"), 10)
        effectiveJava.setId(1L)

        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .items(List.of(
                        new OrderItemCommand(1L, 11))
                )
                .recipient(recipient)
                .build()

        def requestMock = new MockHttpServletRequest("POST", "/orders")
        requestMock.setServerName("localhost")
        requestMock.setServerPort(8080)
        requestMock.setScheme("http")

        def attributes = new ServletRequestAttributes(requestMock)
        RequestContextHolder.setRequestAttributes(attributes)

        def eitherResponse = new PlaceOrderResponse(false, "Too many copies", null)
        manipulateOrderUseCase.placeOrder(placeOrderCommand) >> eitherResponse

        when:
        manipulateOrderUseCase
        ResponseEntity<Object> response = controller.createOrder(placeOrderCommand)

        then:
        response.statusCode == HttpStatus.CREATED
        //response.headers.getLocation().toString() == "http://localhost:8080/orders/${expectedOrderId}"

        cleanup:
        RequestContextHolder.resetRequestAttributes()
    }
}