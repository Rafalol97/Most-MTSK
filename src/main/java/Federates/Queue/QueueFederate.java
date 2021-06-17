package Federates.Queue;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import hla.rti1516e.exceptions.RTIexception;

import java.util.HashMap;


public class QueueFederate extends BaseFederate{

    public void initializeFederate(){
        this.federateType = "QueueFederateType";
    }

    public QueueFederate() throws RTIexception {
        super();

    }

    @Override
    protected void toDoInEachIteration(){

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
      //  addPublication("HLAinteractionRoot.BridgeCalls.BridgeIsFree", "bridgeIsFree");
      //  addPublication("HLAinteractionRoot.CentralCalls.StopQueue", "stopQueue");
        addSubscription("HLAinteractionRoot.BridgeCalls.BridgeIsFree","bridgeIsFree");
    }

    public void receiveFreeState(HashMap<String, String> parameters) throws RTIexception{
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
