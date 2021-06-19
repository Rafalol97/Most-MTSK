package Federates.Bridge;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Utils.Constants;
import Utils.InteractionToBeSend;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;

import java.util.HashMap;


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
            logMe("MOST: wysylam status wolnego mostu z idkierunku: " + currentLights);
            sendFreeState(currentLights); //WYSLI MOST WOLNY i KIERUNEK JAZDY
        }
        else if (lightsTimer == 0)
        {
            logMe("MOST: wysylam status zajetego mostu");
            sendStopQueue(); //WYSLIJ STOP KOLEJKA
        }
        else if (lightsTimer < 0 && carsOnBridge == 0)
        {
            logMe("MOST: resetuje zegar z: " + lightsTimer);
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

        addSubscription("HLAinteractionRoot.CarCalls.IEnteredTheBridge", "iEnteredTheBridge");
        addSubscription("HLAinteractionRoot.CarCalls.ILeftTheBridge", "iLeftTheBridge");
    }

    public void increaseCarsOnBridge(HashMap<String, String> parameters) throws RTIexception {
        carsOnBridge++;
    }

    public void decreaseCarsOnBridge(HashMap<String, String> parameters) throws RTIexception {
        carsOnBridge--;
    }

    private void sendFreeState(Integer bridgeSide) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);
        byte[] bridgeSideBytes = encoderFactory.createHLAASCIIstring(bridgeSide.toString()).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("bridgeIsFree").getInteraction(),"BridgeSide"),bridgeSideBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("bridgeIsFree").getInteraction(),parameters));
    }

    private void sendStopQueue() throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("stopQueue").getInteraction(),parameters));
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new BridgeFederateAmbassador(this);
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
            new BridgeFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }
}
