package pl.borek497.bookstore.demo;

import lombok.Getter;


@Getter
public class HorsesFarm {

    private final int numberOfHorses;

    public HorsesFarm(int numberOfHorses) {
        if (numberOfHorses <= 10) {
            throw new RuntimeException("Farm can not be created with less than 10 horses");
        }
        this.numberOfHorses = numberOfHorses;
    }
}