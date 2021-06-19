package Federates.Car;


import Federates.BaseFederateAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Time;

public class CarFederateAmbassador extends BaseFederateAmbassador {

    public CarFederateAmbassador(CarFederate federate) {
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
            if (interactionClass.equals(federate.getInteractionClassHandle("youCanDriveThrough").getInteraction())) {
                ((Federates.Car.CarFederate) federate).carWithIdCanGo(castParametersToString(theParameters, "youCanDriveThrough"));
            } else if (interactionClass.equals(federate.getInteractionClassHandle("resetLastSpeed").getInteraction())) {
                ((Federates.Car.CarFederate) federate).resetDirection(castParametersToString(theParameters, "resetLastSpeed"));
            }
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
