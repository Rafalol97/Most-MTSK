package models;

import lombok.Getter;
import lombok.Setter;

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
}
