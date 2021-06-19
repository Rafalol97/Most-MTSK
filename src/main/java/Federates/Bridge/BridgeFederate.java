package Federates.Bridge;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Federates.Queue.QueueFederateAmbassador;
import Utils.Constants;
import Utils.InteractionToBeSend;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;


public class BridgeFederate extends BaseFederate{

    int carsOnBridge = 0;

    int lightsTimer = Constants.LIGHT_INTERVAL;

    int currentLights = 0;

    public void initializeFederate(){
        this.federateType = "BridgeFederateType";
    }

    public BridgeFederate()
    {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {
        if(lightsTimer > 0  && carsOnBridge == 0)
        {
            //TODO WYSLI MOST WOLNY i KIERUNEK JAZDY
        }
        else if (lightsTimer == 0)
        {
            //TODO WYSLIJ STOP KOLEJKA
        }
        else if (lightsTimer < 0 && carsOnBridge == 0)
        {
            while(lightsTimer <= 0)
            {
                lightsTimer += Constants.LIGHT_INTERVAL;
                currentLights ^= 1;
            }
        }

        lightsTimer--;
        if(lightsTimer < 0)
        {
            //TODO wyslij do stats i zapisz z czasem operacji
        }
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addPublication("HLAinteractionRoot.BridgeCalls.BridgeIsFree", "bridgeIsFree");
        addPublication("HLAinteractionRoot.BridgeCalls.StopQueue", "stopQueue");

    }

    private void sendFreeState(Integer bridgeSide) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);
        byte[] bridgeSideBytes = encoderFactory.createHLAASCIIstring(bridgeSide.toString()).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("bridgeIsFree").getInteraction(),"BridgeSide"),bridgeSideBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("bridgeIsFree").getInteraction(),parameters));
        System.out.println("InformationSent");
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new BridgeFederateAmbassador(this);
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
            new BridgeFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }
}
