package Federates.Bridge;


import Federates.BaseFederateAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;

public class BridgeFederateAmbassador extends BaseFederateAmbassador {

    public BridgeFederateAmbassador(BridgeFederate federate) {
        super(federate);
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   LogicalTime time,
                                   OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {

        try {
            if (interactionClass.equals(federate.getInteractionClassHandle("iEnteredTheBridge").getInteraction())) {
                ((Federates.Bridge.BridgeFederate) federate).increaseCarsOnBridge(castParametersToString(theParameters, "iEnteredTheBridge"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("iLeftTheBridge").getInteraction())) {
                ((Federates.Bridge.BridgeFederate) federate).decreaseCarsOnBridge(castParametersToString(theParameters, "iLeftTheBridge"));
            }
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }

    }
}
