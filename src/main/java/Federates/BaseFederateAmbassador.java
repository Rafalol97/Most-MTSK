package Federates;

import hla.rti1516e.*;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Time;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link BaseFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
@Getter @Setter
public class BaseFederateAmbassador extends NullFederateAmbassador
{
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    protected BaseFederate federate;

    // these variables are accessible in the package
    protected double federateTime        = 0.0;
    protected double federateLookahead   = 1.0;

    protected boolean isRegulating       = false;
    protected boolean isConstrained      = false;
    protected boolean isAdvancing        = false;

    protected boolean isAnnounced        = false;
    protected boolean isReadyToRun       = false;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public BaseFederateAmbassador(BaseFederate federate )
    {
        this.federate = federate;
    }

    protected void log( String message )
    {
        System.out.println( getFederateTime()+ "  " + message );
    }

//    protected String decodeFlavor( byte[] bytes )
//    {
//        HLAinteger32BE value = federate.encoderFactory.createHLAinteger32BE();
//        // decode
//        try
//        {
//            value.decode( bytes );
//        }
//        catch( DecoderException de )
//        {
//            return "Decoder Exception: "+de.getMessage();
//        }
//
//        switch( value.getValue() )
//        {
//            case 101:
//                return "Cola";
//            case 102:
//                return "Orange";
//            case 103:
//                return "RootBeer";
//            case 104:
//                return "Cream";
//            default:
//                return "Unknown";
//        }
//    }
//
//    protected short decodeNumCups( byte[] bytes )
//    {
//        HLAinteger16BE value = federate.encoderFactory.createHLAinteger16BE();
//        // decode
//        try
//        {
//            value.decode( bytes );
//            return value.getValue();
//        }
//        catch( DecoderException de )
//        {
//            de.printStackTrace();
//            return 0;
//        }
//    }

    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void synchronizationPointRegistrationFailed( String label,
                                                        SynchronizationPointFailureReason reason )
    {
        log( "Failed to register sync point: " + label + ", reason="+reason );
    }

    @Override
    public void synchronizationPointRegistrationSucceeded( String label )
    {
        log( "Successfully registered sync point: " + label );
    }

    @Override
    public void announceSynchronizationPoint( String label, byte[] tag )
    {
        log( "Synchronization point announced: " + label );
        if( label.equals(BaseFederate.READY_TO_RUN) )
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized( String label, FederateHandleSet failed )
    {
        log( "Federation Synchronized: " + label );
        if( label.equals(BaseFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled( LogicalTime time )
    {
        this.federateTime = ((HLAfloat64Time)time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled( LogicalTime time )
    {
        this.federateTime = ((HLAfloat64Time)time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant( LogicalTime time )
    {
        this.federateTime = ((HLAfloat64Time)time).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void discoverObjectInstance( ObjectInstanceHandle theObject,
                                        ObjectClassHandle theObjectClass,
                                        String objectName )
            throws FederateInternalError
    {
        log( "Discoverd Object: handle=" + theObject + ", classHandle=" +
                theObjectClass + ", name=" + objectName );
    }

//    @Override
//    public void reflectAttributeValues( ObjectInstanceHandle theObject,
//                                        AttributeHandleValueMap theAttributes,
//                                        byte[] tag,
//                                        OrderType sentOrder,
//                                        TransportationTypeHandle transport,
//                                        SupplementalReflectInfo reflectInfo )
//            throws FederateInternalError
//    {
//        // just pass it on to the other method for printing purposes
//        // passing null as the time will let the other method know it
//        // it from us, not from the RTI
//        reflectAttributeValues( theObject,
//                theAttributes,
//                tag,
//                sentOrder,
//                transport,
//                null,
//                sentOrder,
//                reflectInfo );
//    }
//
//    @Override
//    public void reflectAttributeValues( ObjectInstanceHandle theObject,
//                                        AttributeHandleValueMap theAttributes,
//                                        byte[] tag,
//                                        OrderType sentOrdering,
//                                        TransportationTypeHandle theTransport,
//                                        LogicalTime time,
//                                        OrderType receivedOrdering,
//                                        SupplementalReflectInfo reflectInfo )
//            throws FederateInternalError
//    {
//        StringBuilder builder = new StringBuilder( "Reflection for object:" );
//
//        // print the handle
//        builder.append( " handle=" + theObject );
//        // print the tag
//        builder.append( ", tag=" + new String(tag) );
//        // print the time (if we have it) we'll get null if we are just receiving
//        // a forwarded call from the other reflect callback above
//        if( time != null )
//        {
//            builder.append( ", time=" + ((HLAfloat64Time)time).getValue() );
//        }
//
//        // print the attribute information
//        builder.append( ", attributeCount=" + theAttributes.size() );
//        builder.append( "\n" );
//        for( AttributeHandle attributeHandle : theAttributes.keySet() )
//        {
//            // print the attibute handle
//            builder.append( "\tattributeHandle=" );
//
//            // if we're dealing with Flavor, decode into the appropriate enum value
//            if( attributeHandle.equals(federate.flavHandle) )
//            {
//                builder.append( attributeHandle );
//                builder.append( " (Flavor)    " );
//                builder.append( ", attributeValue=" );
//                builder.append( decodeFlavor(theAttributes.get(attributeHandle)) );
//            }
//            else if( attributeHandle.equals(federate.cupsHandle) )
//            {
//                builder.append( attributeHandle );
//                builder.append( " (NumberCups)" );
//                builder.append( ", attributeValue=" );
//                builder.append( decodeNumCups(theAttributes.get(attributeHandle)) );
//            }
//            else
//            {
//                builder.append( attributeHandle );
//                builder.append( " (Unknown)   " );
//            }
//
//            builder.append( "\n" );
//        }
//
//        //log( builder.toString() );
//    }

    @Override
    public void receiveInteraction( InteractionClassHandle interactionClass,
                                    ParameterHandleValueMap theParameters,
                                    byte[] tag,
                                    OrderType sentOrdering,
                                    TransportationTypeHandle theTransport,
                                    SupplementalReceiveInfo receiveInfo )
            throws FederateInternalError
    {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        this.receiveInteraction( interactionClass,
                theParameters,
                tag,
                sentOrdering,
                theTransport,
                null,
                sentOrdering,
                receiveInfo );
    }

    @Override
    public void receiveInteraction( InteractionClassHandle interactionClass,
                                    ParameterHandleValueMap theParameters,
                                    byte[] tag,
                                    OrderType sentOrdering,
                                    TransportationTypeHandle theTransport,
                                    LogicalTime time,
                                    OrderType receivedOrdering,
                                    SupplementalReceiveInfo receiveInfo )
            throws FederateInternalError
    {
        StringBuilder builder = new StringBuilder( "Interaction Received:" );

        // print the handle
        builder.append( " handle=" + interactionClass );
        if( interactionClass.equals(federate.servedHandle) )
        {
            builder.append( " (DrinkServed)" );
        }

        // print the tag
        builder.append( ", tag=" + new String(tag) );
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if( time != null )
        {
            builder.append( ", time=" + ((HLAfloat64Time)time).getValue() );
        }

        // print the parameer information
        builder.append( ", parameterCount=" + theParameters.size() );
        builder.append( "\n" );
        for( ParameterHandle parameter : theParameters.keySet() )
        {
            // print the parameter handle
            builder.append( "\tparamHandle=" );
            builder.append( parameter );
            // print the parameter value
            builder.append( ", paramValue=" );
            builder.append( theParameters.get(parameter).length );
            builder.append( " bytes" );
            builder.append( "\n" );
        }

        //log( builder.toString() );
    }

    public HashMap<String, String> castParametersToString(ParameterHandleValueMap theParameters, String iName) throws RTIexception{
        HashMap<String, String> parameters = new HashMap<>();
        for( ParameterHandle parameter : theParameters.keySet() ) {
            parameters.put(federate.getRtiamb().getParameterName(federate.getInteractionClassHandle(iName).getInteraction(),parameter),decodeString(theParameters.get(parameter)));
        }
        return parameters;
    }

    protected String decodeString(byte[] bytes){
        HLAASCIIstring value = federate.encoderFactory.createHLAASCIIstring();
        // decode
        try
        {
            value.decode( bytes );
        }
        catch( DecoderException de )
        {
            return "Decoder Exception: "+de.getMessage();
        }
        return value.getValue();
    }

    @Override
    public void removeObjectInstance( ObjectInstanceHandle theObject,
                                      byte[] tag,
                                      OrderType sentOrdering,
                                      SupplementalRemoveInfo removeInfo )
            throws FederateInternalError
    {
        log( "Object Removed: handle=" + theObject );
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
}