package Federates;

import Utils.InteractionToBeSend;
import Utils.InteractionWithType;
import hla.rti1516e.*;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static Utils.Constants.*;


@Getter
public abstract class BaseFederate
{
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------
    /** The sync point all federates will sync up on before starting */
    public static final String READY_TO_RUN = "ReadyToRun";

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    protected RTIambassador rtiamb;
    protected BaseFederateAmbassador fedamb;  // created when we connect
    protected HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join
    protected String federateName;
    // caches of handle types - set once we join a federation
    protected ObjectClassHandle sodaHandle;
    protected AttributeHandle cupsHandle;
    protected AttributeHandle flavHandle;
    protected InteractionClassHandle servedHandle;

    private final HashMap<String, InteractionWithType> interactionClassHandles = new HashMap<>();

    protected ArrayList<InteractionToBeSend> interactionsToSend;
    protected String federateType;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    public BaseFederate() {
        //fill with reading configs
        interactionsToSend = new ArrayList<>();
    }
    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    public InteractionWithType getInteractionClassHandle(String hash){
        return interactionClassHandles.get(hash);
    }
    protected void addInteractionClassHandle(String hash, InteractionClassHandle handle, boolean published, boolean subscribed){
        if(interactionClassHandles.containsKey(hash)){
            if(published)
                interactionClassHandles.get(hash).setPublished(true);
            if(subscribed)
                interactionClassHandles.get(hash).setSubscribed(true);
        }else{
            interactionClassHandles.put(hash, new InteractionWithType(handle, published, subscribed));
        }
    }

    /**This is just a helper method to make sure all logging it output in the same form*/
    protected void log(String message) {
        System.out.println(federateName+"\t: " + message);
    }

    protected void logMe(String message) {
        System.out.println(fedamb.getFederateTime() + "\t: " + message);
    }
    /**This method will block until the user presses enter*/
    protected void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    public void runFederate(String federateName) throws Exception {
        this.initializeFederate();
        this.federateName=federateName;
        /////////////////////////////////////////////////
        // 1 & 2. create the RTIambassador and Connect //
        /////////////////////////////////////////////////
        log("Creating RTIambassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        // connect
        log("Connecting...");
        fedamb = newFedAmb();
        rtiamb.connect(fedamb, CallbackModel.HLA_IMMEDIATE);  // deliver callbacks as soon as they arrive
        //rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );   // deliver callbacks when we call evoke

        //////////////////////////////
        // 3. create the federation //
        //////////////////////////////
        log("Creating Federation...");
        // We attempt to create a new federation with the first three of the
        // restaurant FOM modules covering processes, food and drink
        try {
            URL[] modules = new URL[]{
                    (new File("foms/objects.xml")).toURI().toURL(),
                    (new File("foms/interactions.xml")).toURI().toURL(),
                    (new File("foms/general.xml")).toURI().toURL()
            };

            rtiamb.createFederationExecution(FEDERATION_NAME, modules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception loading one of the FOM modules from disk: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }
        ////////////////////////////
        // 4. join the federation //
        ////////////////////////////
        URL[] joinModules = new URL[]{
                (new File("foms/objects.xml")).toURI().toURL()
        };

        rtiamb.joinFederationExecution(federateName,            // name for the federate
                federateType,                                   // federate type
                FEDERATION_NAME,                                // name of federation
                joinModules);                                   // modules we want to add

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();

        waitForUser();

        ////////////////////////////////
        // 5. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        // wait until the point is announced
        while (!fedamb.isAnnounced()) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
            System.out.println("Still waiting for announce");
        }

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (!fedamb.isReadyToRun()) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        enableTimePolicy();
        log("Time Policy Enabled");

        addPublicationsAndSubscriptions();
        publishAndSubscribe();

        log("Published and Subscribed");
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            toDoInEachIteration();

            ArrayList<InteractionToBeSend> tempInteractions = (ArrayList<InteractionToBeSend>) interactionsToSend.clone();
            interactionsToSend.clear();
            for (InteractionToBeSend interaction: tempInteractions) {
                sendInteraction(interaction.getInteractionClassHandle(), interaction.getParameters());
            }

            advanceTime(1.0);

            if(USE_DELAY){
                Thread.sleep(ITERATION_DELAY);
            }
        }
        lastTaskBeforeDestroy();

        rtiamb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");
        try {
            rtiamb.destroyFederationExecution(FEDERATION_NAME);
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }

        try {
            rtiamb.disconnect();
            log("Disconnected");
        } catch (Exception e) {
            log("Exception while disconnecting");
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */
    protected void enableTimePolicy() throws Exception
    {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval( fedamb.federateLookahead );

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiamb.enableTimeRegulation( lookahead );

        // tick until we get the callback
        while(!fedamb.isRegulating)
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while(!fedamb.isConstrained)
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
    }
    /**
     * This method will inform the RTI about the types of data that the federate will
     * be creating, and the types of data we are interested in hearing about as other
     * federates produce it.
     */

    protected void addSubscription(String iName, String hName) throws FederateNotExecutionMember, NameNotFound, NotConnected, RTIinternalError {
        addInteractionClassHandle(hName, rtiamb.getInteractionClassHandle(iName), false, true);
    }
    protected void addPublication(String iName, String hName) throws FederateNotExecutionMember, NameNotFound, NotConnected, RTIinternalError {
        addInteractionClassHandle(hName, rtiamb.getInteractionClassHandle(iName), true, false);
    }

    protected void publishAndSubscribe() throws RTIexception
    {
        for (InteractionWithType interaction:interactionClassHandles.values()) {
            if(interaction.isPublished()){
                rtiamb.publishInteractionClass(interaction.getInteraction());
            }
            if(interaction.isSubscribed()){
                rtiamb.subscribeInteractionClass(interaction.getInteraction());
            }
        }
    }
    /**
     * This method will register an instance of the Soda class and will
     * return the federation-wide unique handle for that instance. Later in the
     * simulation, we will update the attribute values for this instance
     */
    private ObjectInstanceHandle registerObject() throws RTIexception
    {
        return rtiamb.registerObjectInstance( sodaHandle );
    }

    /**
     * This method will update all the values of the given object instance. It will
     * set the flavour of the soda to a random value from the options specified in
     * the FOM (Cola - 101, Orange - 102, RootBeer - 103, Cream - 104) and it will set
     * the number of cups to the same value as the current time.
     * <p/>
     * Note that we don't actually have to update all the attributes at once, we
     * could update them individually, in groups or not at all!
     */
    private void updateAttributeValues( ObjectInstanceHandle objectHandle ) throws RTIexception
    {
        ///////////////////////////////////////////////
        // create the necessary container and values //
        ///////////////////////////////////////////////
        // create a new map with an initial capacity - this will grow as required
        AttributeHandleValueMap attributes = rtiamb.getAttributeHandleValueMapFactory().create(2);

        // create the collection to store the values in, as you can see
        // this is quite a lot of work. You don't have to use the encoding
        // helpers if you don't want. The RTI just wants an arbitrary byte[]

        // generate the value for the number of cups (same as the timestep)
        HLAinteger16BE cupsValue = encoderFactory.createHLAinteger16BE( getTimeAsShort() );
        attributes.put( cupsHandle, cupsValue.toByteArray() );

        // generate the value for the flavour on our magically flavour changing drink
        // the values for the enum are defined in the FOM
        int randomValue = 101 + new Random().nextInt(3);
        HLAinteger32BE flavValue = encoderFactory.createHLAinteger32BE( randomValue );
        attributes.put( flavHandle, flavValue.toByteArray() );

        //////////////////////////
        // do the actual update //
        //////////////////////////
        rtiamb.updateAttributeValues( objectHandle, attributes, generateTag() );

        // note that if you want to associate a particular timestamp with the
        // update. here we send another update, this time with a timestamp:
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.updateAttributeValues( objectHandle, attributes, generateTag(), time );
    }

    /**
     * This method will send out an interaction of the type FoodServed.DrinkServed. Any
     * federates which are subscribed to it will receive a notification the next time
     * they tick(). This particular interaction has no parameters, so you pass an empty
     * map, but the process of encoding them is the same as for attributes.
     */
    protected void sendInteraction(InteractionClassHandle interactionType, ParameterHandleValueMap parameters) throws RTIexception {
        HLAfloat64Time time = timeFactory.makeTime(fedamb.getFederateTime() + fedamb.getFederateLookahead());
        rtiamb.sendInteraction(interactionType, parameters, generateTag(), time);
    }

    /**
     * This method will request a time advance to the current time, plus the given
     * timestep. It will then wait until a notification of the time advance grant
     * has been received.
     */
    protected void advanceTime( double timestep ) throws RTIexception
    {
        // request the advance
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( time );

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while( fedamb.isAdvancing )
        {
            rtiamb.evokeMultipleCallbacks( 0.0001, 0.2 );
        }
    }

    /**
     * This method will attempt to delete the object instance of the given
     * handle. We can only delete objects we created, or for which we own the
     * privilegeToDelete attribute.
     */
    private void deleteObject( ObjectInstanceHandle handle ) throws RTIexception
    {
        rtiamb.deleteObjectInstance( handle, generateTag() );
    }

    protected short getTimeAsShort()
    {
        return (short)fedamb.federateTime;
    }

    protected byte[] generateTag()
    {
        return ("(timestamp) "+System.currentTimeMillis()).getBytes();
    }

    protected abstract void addPublicationsAndSubscriptions() throws RTIexception;
    protected abstract BaseFederateAmbassador newFedAmb();
    protected abstract void initializeFederate();
    protected abstract void toDoInEachIteration() throws RTIexception;
    protected void lastTaskBeforeDestroy(){

    }
}