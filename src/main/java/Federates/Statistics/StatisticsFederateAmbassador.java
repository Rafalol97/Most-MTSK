package Federates.Statistics;

import Federates.BaseFederateAmbassador;
import Federates.GUI.GUIFederate;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;

public class StatisticsFederateAmbassador extends BaseFederateAmbassador {

    public StatisticsFederateAmbassador(StatisticsFederate federate) {
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
            if (interactionClass.equals(federate.getInteractionClassHandle("sendQueueData").getInteraction())) {
                ((StatisticsFederate) federate).receiveQueueData(castParametersToString(theParameters, "sendQueueData"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("sendBridgeData").getInteraction())) {
                ((StatisticsFederate) federate).receiveBridgeData(castParametersToString(theParameters, "sendBridgeData"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("sendCarData").getInteraction())) {
                ((StatisticsFederate) federate).receiveCarData(castParametersToString(theParameters, "sendCarData"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("weWantToDriveThrough").getInteraction())) {
                ((StatisticsFederate) federate).receiveWeWantToDriveThrough(castParametersToString(theParameters, "weWantToDriveThrough"));

            }
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
