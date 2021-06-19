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
        StringBuilder builder = new StringBuilder("Interaction Received:");



        builder.append(" handle=" + interactionClass);
        if (interactionClass.equals(federate.getInteractionClassHandle("youCanDriveThrough").getInteraction())) {
            try {
                ((Federates.Car.CarFederate) federate).carWithIdCanGo(castParametersToString(theParameters, "youCanDriveThrough"));
            } catch (RTIexception rtIexception) {
                rtIexception.printStackTrace();
            }
            builder.append(" (Bridge side is set free)");
        }
        else if (interactionClass.equals(federate.getInteractionClassHandle("resetLastSpeed").getInteraction())) {
            try {
                ((Federates.Car.CarFederate) federate).resetDirection(castParametersToString(theParameters, "resetLastSpeed"));
            } catch (RTIexception rtIexception) {
                rtIexception.printStackTrace();
            }
            builder.append(" (Bridge side is set free)");
        }

        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=" + theParameters.size());
        builder.append("\n");
        for (ParameterHandle parameter : theParameters.keySet()) {
            // print the parameter handle
            builder.append("\tparamHandle=");
            builder.append(parameter);
            // print the parameter value
            builder.append(", paramValue=");
            builder.append(theParameters.get(parameter).length);
            builder.append(" bytes");
            builder.append("\n");
        }

        //log(builder.toString());
    }
}
