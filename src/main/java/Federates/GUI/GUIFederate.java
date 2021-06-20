package Federates.GUI;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import hla.rti1516e.exceptions.RTIexception;
import javafx.application.Platform;
import models.Car;

import java.util.ArrayList;
import java.util.HashMap;


public class GUIFederate extends BaseFederate{

    public BridgeGUI bridgeGUI;
    public ArrayList<Car> cars;

    public void initializeFederate(){
        this.federateType = "GUIFederateType";
        bridgeGUI = new BridgeGUI();
        BridgeGUI.main(new String[]{""});

    }

    public GUIFederate() throws RTIexception {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addSubscription("HLAinteractionRoot.CarCalls.SendCarData", "sendCarData");
        addSubscription("HLAinteractionRoot.StatisticsCalls.SendStats", "sendStats");
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new GUIFederateAmbassador(this);
    }

    public void receiveStartedcars(HashMap<String, String> sendCarData) {
        cars = Car.makeCarModel(sendCarData.get("carIds"),sendCarData.get("carSpeeds"),sendCarData.get("carCurrentStates"),sendCarData.get("carSides"));
    }

    public void receiveStats(HashMap<String, String> sendStats) {
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
            new GUIFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }


}
