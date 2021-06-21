package Federates.GUI;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Federates.Bridge.BridgeFederate;
import hla.rti1516e.exceptions.RTIexception;
import javafx.application.Platform;
import models.Car;

import java.util.ArrayList;
import java.util.HashMap;


public class GUIFederate extends BaseFederate {

    public ArrayList<Car> cars;

    public ArrayList<Integer> StartedCarsSize = new ArrayList<>();
    public Integer OverallQueue1Size = 0;
    public Integer OverallQueue2Size = 0;
    public ArrayList<Integer> Queue1Size = new ArrayList<>();
    public ArrayList<Integer> Queue2Size = new ArrayList<>();
    public ArrayList<Integer> LightsTimer = new ArrayList<>();
    public ArrayList<Integer> BridgeSide = new ArrayList<>();
    public ArrayList<Integer> GeneratedCars = new ArrayList<>();

    public void initializeFederate() {
        this.federateType = "GUIFederateType";
    }

    public GUIFederate() throws RTIexception {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {

        BridgeGUI.addSqToBridgePane(cars);

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addSubscription("HLAinteractionRoot.CarCalls.SendCarData", "sendCarData");
        addSubscription("HLAinteractionRoot.StatisticsCalls.SendStats", "sendStats");
    }

    @Override
    protected BaseFederateAmbassador newFedAmb() {
        return new GUIFederateAmbassador(this);
    }

    public void receiveStartedcars(HashMap<String, String> sendCarData) {
        cars = Car.makeCarModel(sendCarData.get("carIds"), sendCarData.get("carSpeeds"), sendCarData.get("carCurrentStates"), sendCarData.get("carSides"));
    }

    public void receiveStats(HashMap<String, String> parameters) {
        StartedCarsSize = makeArray(parameters.get("StartedCarsSize").split(","));
        OverallQueue1Size = Integer.parseInt(parameters.get("OverallQueue1Size"));
        OverallQueue2Size = Integer.parseInt(parameters.get("OverallQueue2Size"));
        Queue1Size = makeArray(parameters.get("Queue1Size").split(","));
        Queue2Size = makeArray(parameters.get("Queue2Size").split(","));
        LightsTimer = makeArray(parameters.get("LightsTimer").split(","));
        BridgeSide = makeArray(parameters.get("BridgeSide").split(","));
        GeneratedCars = makeArray(parameters.get("GeneratedCars").split(","));
    }

    public static void main(String[] args) {
        String federateName = "exampleFederate";
        if (args.length != 0) {
            federateName = args[0];
        }
        try {
            new GUIFederate().runFederate(federateName);
        } catch (Exception rtie) {
            rtie.printStackTrace();
        }
    }

    public ArrayList<Integer> makeArray(String[] tmpStringArray)
    {
        ArrayList<Integer> tmpArray = new ArrayList<>();
        for(String string:tmpStringArray)
        {
            if(string != "null")
                tmpArray.add(Integer.parseInt(string));
        }
        return tmpArray;
    }

}
