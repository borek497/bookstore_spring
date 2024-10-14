package pl.borek497.bookstore.demo;

import lombok.Getter;

@Getter
public class HorsesFarm {

    private final int numberOfHorses;

    public HorsesFarm(int numberOfHorses) {
        if (numberOfHorses <= 10) {
            throw new TooSmallHorsesException();
        }
        this.numberOfHorses = numberOfHorses;
    }
}