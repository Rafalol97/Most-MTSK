package Federates.GUI;

import Federates.BaseFederateAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import org.apache.http.cookie.params.CookieSpecPNames;

public class GUIFederateAmbassador extends BaseFederateAmbassador {

    public GUIFederateAmbassador(GUIFederate federate) {
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
        System.out.println(interactionClass.toString());
        try {
            if (interactionClass.equals(federate.getInteractionClassHandle("sendCarData").getInteraction())) {
                ((GUIFederate) federate).receiveStartedcars(castParametersToString(theParameters, "sendCarData"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("sendStats").getInteraction())) {
                ((GUIFederate) federate).receiveStats(castParametersToString(theParameters, "sendStats"));
            }
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
