package Federates.Statistics;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import hla.rti1516e.exceptions.RTIexception;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class StatisticsFederate extends BaseFederate {


    public void initializeFederate(){
        this.federateType = "StatisticsFederateType";
    }


    public static ArrayList<Integer> StartedCarsSize = new ArrayList<>();
    public static Integer OverallQueue1Size = 0;
    public static Integer OverallQueue2Size = 0;
    public static ArrayList<Integer> Queue1Size = new ArrayList<>();
    public static ArrayList<Integer> Queue2Size = new ArrayList<>();
    public static ArrayList<Integer> LightsTimer = new ArrayList<>();
    public static ArrayList<Integer> BridgeSide = new ArrayList<>();
    public static ArrayList<Integer> GeneratedCars = new ArrayList<>();

    private Integer currentGeneratedNumberOfCars = 0;

    public StatisticsFederate() throws RTIexception {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {
        GeneratedCars.add(currentGeneratedNumberOfCars);
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addSubscription("HLAinteractionRoot.CarCalls.SendCarData", "sendCarData");
        addSubscription("HLAinteractionRoot.QueueCalls.SendQueueData", "sendQueueData");
        addSubscription("HLAinteractionRoot.BridgeCalls.SendBridgeData", "sendBridgeData");
        addSubscription("HLAinteractionRoot.CarCalls.WeWantToDriveThrough", "weWantToDriveThrough");
    }

    public void receiveCarData(HashMap<String, String> parameters) throws RTIexception {
        String startedCarIds = parameters.get("carIds");
        StartedCarsSize.add(startedCarIds.split(",").length);
    }

    public void receiveQueueData(HashMap<String, String> parameters) throws RTIexception {
        Queue1Size.add(Integer.parseInt(parameters.get("Queue1Size")));
        Queue2Size.add(Integer.parseInt(parameters.get("Queue2Size")));
    }

    public void receiveBridgeData(HashMap<String, String> parameters) throws RTIexception {
        BridgeSide.add(Integer.parseInt(parameters.get("BridgeSide")));
        int lightsValue = Integer.parseInt(parameters.get("LightsTimer"));
        if(lightsValue<0)
            LightsTimer.add(-1);
        else
            LightsTimer.add(0);
    }

    public void receiveWeWantToDriveThrough(HashMap<String, String> parameters) {
        ArrayList<CarViewModel> newCars = CarViewModel.makeCarsViewModel(parameters.get("carIds"), parameters.get("directionIds"));
        for (CarViewModel car : newCars) {
           if(car.getSide()==0){
               OverallQueue1Size++;
           }
           else OverallQueue2Size++;
        }
        currentGeneratedNumberOfCars += newCars.size();
    }

    @Override
    protected void lastTaskBeforeDestroy() {
        logMe("Q1S: " +Queue1Size + " Q2S: " +Queue2Size + " LTIMER: " +LightsTimer+ " SIDE: " + BridgeSide);
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new StatisticsFederateAmbassador(this);
    }

    public static void main( String[] args )
    {
        String federateName = "exampleFederate";
        if( args.length != 0 )
        {
            federateName = args[0];
        }
        try
        {
            new StatisticsFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }


}
