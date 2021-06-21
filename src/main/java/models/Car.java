package models;

import Utils.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

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

    public Car(int id, double speed, double currentState, int side)
    {
        this.id = id;
        this.speed = speed;
        this.side = side;
        this.currentState = currentState;
    }

    public static ArrayList<String> makeFullCarStrings(ArrayList<Integer> carsId, ArrayList<Car> cars){

        ArrayList<Car> returnCarList = new ArrayList<>();
        for (Integer id: carsId) {
            returnCarList.add(cars.get(id));
        }
        ArrayList<String> stringsToSend = new ArrayList<>();
        ArrayList<Integer> tmpIdsList = new ArrayList<>();
        ArrayList<Integer> tmpSidesList = new ArrayList<>();
        ArrayList<Double> tmpCurrentStateList = new ArrayList<>();
        ArrayList<Double> tmpSpeedList = new ArrayList<>();
        for (Car car: returnCarList)
        {
            tmpIdsList.add(car.getId());
            tmpSidesList.add(car.getSide());
            tmpCurrentStateList.add(car.getCurrentState());
            tmpSpeedList.add(car.getSpeed());
        }
        stringsToSend.add(tmpIdsList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        stringsToSend.add(tmpSpeedList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        stringsToSend.add(tmpCurrentStateList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        stringsToSend.add(tmpSidesList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return stringsToSend;
    }

    public static ArrayList<Car> makeCarModel(String carIdsString,String speedString, String currentStateString, String directionIdsString)
    {
        if(carIdsString==null || carIdsString.isEmpty()) return null;
        ArrayList<Car> receivedCars = new ArrayList<>();
        String[] carsIds = carIdsString.split(",");
        String[] directionIds = directionIdsString.split(",");
        String[] speedStrings = speedString.split(",");
        String[] currentStateStrings = currentStateString.split(",");
        for(int i = 0; i < carsIds.length; i++) {
            receivedCars.add(new Car(Integer.parseInt(carsIds[i]),Double.parseDouble(speedStrings[i]),Double.parseDouble(currentStateStrings[i]), Integer.parseInt(directionIds[i])));
        }
        return receivedCars;
    }
}
