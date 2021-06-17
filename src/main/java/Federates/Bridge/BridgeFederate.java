package Federates.Bridge;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Federates.Queue.QueueFederateAmbassador;
import Utils.InteractionToBeSend;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;


public class BridgeFederate extends BaseFederate{

    public void initializeFederate(){
        this.federateType = "BridgeFederateType";
    }

    public BridgeFederate()
    {
        super();


    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {
        if(this.fedamb.getFederateTime()==4.0){
            sendFreeState(1);
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
