package pl.borek497.bookstore.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Cities {
    private final String cityName;
    private final double population;
}