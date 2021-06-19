package models;

import Utils.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Car {
    int id;
    double speed;
    int side;
    double currentState = 0.0;

    boolean started = false;
    double startedTime = 0.0;

    boolean finished = false;
    double finishedTime = 0.0;

    public Car(int id, Random randomizer)
    {
        this.id = id;
        this.speed = Constants.minCarSpeed + (Constants.maxCarSpeed - Constants.minCarSpeed) * randomizer.nextDouble();
        this.side = (int)Math.round(Math.random());
    }
}
