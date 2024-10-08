package pl.borek497.bookstore.clock;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemClock implements Clock {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}