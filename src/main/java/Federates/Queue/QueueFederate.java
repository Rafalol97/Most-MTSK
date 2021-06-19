package Federates.Queue;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import hla.rti1516e.exceptions.RTIexception;
import models.Car;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class QueueFederate extends BaseFederate{

    ArrayList<Integer> Queue1 = new ArrayList<>();
    ArrayList<Integer> Queue2 = new ArrayList<>();

    double currentSpeed = 0.0;
    int currentSide;

    ArrayList<CarViewModel> recivedCars = new ArrayList<>();

    int currentQueue;
    boolean bridgeIsFree = false;

    public void initializeFederate(){
        this.federateType = "QueueFederateType";
    }

    public QueueFederate() throws RTIexception {
        super();

    }

    @Override
    protected void toDoInEachIteration() {
        if(recivedCars.size() != 0)
        {
            for(CarViewModel car:recivedCars)
            {
                if(car.getSide() == 1)
                    Queue1.add(car.getId());
                else
                    Queue2.add(car.getId());
            }
            recivedCars.clear();
        }
        if(bridgeIsFree)
        {
            if(currentSide == 1 && Queue1.size() != 0)
            {
                int carIdToStart = Queue1.get(0);
                Queue1.remove(0);

                //TODO wyślij id auta to wystartowania do carFederate
            }
            else if(Queue2.size() != 0)
            {
                int carIdToStart = Queue2.get(0);
                Queue2.remove(0);

                //TODO wyślij id auta to wystartowania do carFederate
            }
        }
        else System.out.printf("");
            //TODO wyslij do autFedereta reset lastCarSpeed
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
      //  addPublication("HLAinteractionRoot.BridgeCalls.BridgeIsFree", "bridgeIsFree");
      //  addPublication("HLAinteractionRoot.CentralCalls.StopQueue", "stopQueue");
        addSubscription("HLAinteractionRoot.BridgeCalls.BridgeIsFree","bridgeIsFree");
    }

    public void receiveFreeState(HashMap<String, String> parameters) throws RTIexception {
       String bridgeSide  = parameters.get("BridgeSide");
       String clientPhone = "", districtID = "";
       System.out.println("STRONA JEST WOLNA WOHHOO");
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new QueueFederateAmbassador(this);
    }


    public static void main( String[] args )
    {
        // get a federate name, use "exampleFederate" as default
        String federateName = "exampleFederate";
        if( args.length != 0 )
        {
            federateName = args[0];
        }
        try
        {
            new QueueFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }
}
