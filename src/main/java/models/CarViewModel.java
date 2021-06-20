package models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CarViewModel {
    int id;
    int side;

    public CarViewModel(int id, int side)
    {
        this.id = id;
        this.side = side;
    }

    public static ArrayList<CarViewModel> makeCarsViewModel(String carIdsString, String directionIdsString)
    {
        ArrayList<CarViewModel> receivedCars = new ArrayList<>();

        String[] carsIds = carIdsString.split(",");
        String[] directionIds = directionIdsString.split(",");

        for(int i = 0; i < carsIds.length; i++) {
            receivedCars.add(new CarViewModel(Integer.parseInt(carsIds[i]), Integer.parseInt(directionIds[i])));
        }
        return receivedCars;
    }
}
