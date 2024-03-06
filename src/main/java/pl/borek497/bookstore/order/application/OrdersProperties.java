package pl.borek497.bookstore.order.application;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Value
@ConfigurationProperties("app.orders")//prefix klucza pod którym znajdują się nasze propertisy
public class OrdersProperties {
    String abandonCron;
    Duration paymentPeriod;
}