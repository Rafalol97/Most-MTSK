package Federates.GUI;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Federates.Bridge.BridgeFederate;
import Utils.Constants;
import hla.rti1516e.exceptions.RTIexception;
import javafx.application.Platform;
import models.Car;

import java.util.ArrayList;
import java.util.HashMap;


public class GUIFederate extends BaseFederate {

    public ArrayList<Car> cars;
    public Integer iteration = 0;

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
    protected void iterationSequence() throws RTIexception {
        BridgeGUI.addSqToBridgePane(cars);
        iteration++;
        if(iteration % Constants.GUI_STATISTICS_UPDATE_INTERVAL==0){
            BridgeGUI.updateStatistics(Queue1Size,Queue2Size,OverallQueue1Size,OverallQueue2Size,GeneratedCars,LightsTimer,StartedCarsSize);
        }
        if(Queue1Size!=null && Queue2Size !=null && Queue1Size.size()>0 && Queue2Size.size()>0) {
            BridgeGUI.UpdateQueueData(Queue1Size.get(Queue1Size.size() - 1), Queue2Size.get(Queue2Size.size() - 1));
        }
       BridgeGUI.UpdateLights();

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addSubscription("HLAinteractionRoot.CarCalls.SendCarData", "sendCarData");
        addSubscription("HLAinteractionRoot.StatisticsCalls.SendStats", "sendStats");
        addSubscription("HLAinteractionRoot.BridgeCalls.StopQueue", "stopQueue");
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
        if(tmpStringArray[0].isEmpty())return null;
        ArrayList<Integer> tmpArray = new ArrayList<>();
        for(String string:tmpStringArray)
        {
            tmpArray.add(Integer.parseInt(string));
        }
        return tmpArray;
    }

    public void receiveStop(HashMap<String, String> stopQueue) {
        BridgeGUI.Stop();

    }
}
