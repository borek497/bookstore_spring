import org.junit.platform.commons.function.Try
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.borek497.bookstore.commons.Either
import pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase
import pl.borek497.bookstore.order.application.port.QueryOrderUseCase
import pl.borek497.bookstore.order.domain.Recipient
import pl.borek497.bookstore.order.web.OrderController
import pl.borek497.bookstore.security.UserSecurity
import spock.lang.Specification

import java.util.function.Function

import static pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.*
import static pl.borek497.bookstore.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand.*


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
                .items([orderItemCommand1, orderItemCommand2])
                .recipient(recipient)
                .build()

        def expectedOrderId = 123L
        def expectedUri = URI.create("/orders/${expectedOrderId}")

        and:
        controller.orderUri(expectedOrderId) >> expectedUri

        def placeOrderResponse = PlaceOrderResponse.success(expectedOrderId)

        manipulateOrderUseCase.placeOrder(placeOrderCommand) >> placeOrderResponse

        when:
        ResponseEntity<Object> response = controller.createOrder(placeOrderCommand)

        then:
        response.statusCode == HttpStatus.CREATED
        response.headers.location == expectedUri
    }







}