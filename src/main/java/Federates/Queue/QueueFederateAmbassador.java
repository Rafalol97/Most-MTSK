package Federates.Queue;

import Federates.BaseFederateAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;

public class QueueFederateAmbassador extends BaseFederateAmbassador {

    public QueueFederateAmbassador(QueueFederate federate) {
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
            if (interactionClass.equals(federate.getInteractionClassHandle("bridgeIsFree").getInteraction())) {
                ((QueueFederate) federate).receiveFreeState(castParametersToString(theParameters, "bridgeIsFree"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("stopQueue").getInteraction())) {
                ((QueueFederate) federate).receiveStopQueue(castParametersToString(theParameters, "stopQueue"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("weWantToDriveThrough").getInteraction())) {
                ((QueueFederate) federate).receiveCars(castParametersToString(theParameters, "weWantToDriveThrough"));
            }
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
