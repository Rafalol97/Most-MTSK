package Federates.Queue;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Utils.InteractionToBeSend;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;
import models.Car;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class QueueFederate extends BaseFederate{

    ArrayList<Integer> Queue1 = new ArrayList<>();
    ArrayList<Integer> Queue2 = new ArrayList<>();

    double currentSpeed = 0.0;
    int currentSide;

    ArrayList<CarViewModel> recivedCars = new ArrayList<>();

    boolean bridgeIsFree = false;

    public void initializeFederate(){
        this.federateType = "QueueFederateType";
    }

    public QueueFederate() throws RTIexception {
        super();

    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {
        if(recivedCars.size() != 0)
        {
            logMe("KOLEJKA: Dodaje samochody w kolejce: " + recivedCars.toString());
            for(CarViewModel car:recivedCars)
            {
                if(car.getSide() == 1)
                    Queue1.add(car.getId());
                else
                    Queue2.add(car.getId());
            }
            recivedCars.clear();

            //TODO ZAKTUALIZAUJ STANY KOLEJEK W GUI
        }
        if(bridgeIsFree)
        {
            logMe("KOLEJKA: Wolny most wiec jazda!");
            if(currentSide == 1 && Queue1.size() != 0)
            {
                int carIdToStart = Queue1.get(0);
                Queue1.remove(0);
                logMe("KOLEJKA: Teraz samochod: " + carIdToStart);
                sentCarIdThatCanStart(carIdToStart); //TODO wyślij id auta to wystartowania do carFederate
            }
            else if(Queue2.size() != 0)
            {
                int carIdToStart = Queue2.get(0);
                Queue2.remove(0);
                logMe("KOLEJKA: Teraz samochod: " + carIdToStart);
                sentCarIdThatCanStart(carIdToStart); //TODO wyślij id auta to wystartowania do carFederate
            }
        }
        else
        {
            logMe("KOLEJKA: wysyłam reset predkosci (czyli most zajety, zmiana stron!)");
            sentResetLastSpeed(); //TODO wyslij do autFedereta reset lastCarSpeed
        }
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addPublication("HLAinteractionRoot.QueueCalls.YouCanDriveThrough","youCanDriveThrough");
        addPublication("HLAinteractionRoot.QueueCalls.ResetLastSpeed","resetLastSpeed");

        addSubscription("HLAinteractionRoot.BridgeCalls.BridgeIsFree","bridgeIsFree");
        addSubscription("HLAinteractionRoot.BridgeCalls.StopQueue", "stopQueue");
        addSubscription("HLAinteractionRoot.CarCalls.WeWantToDriveThrough", "weWantToDriveThrough");
    }

    public void receiveFreeState(HashMap<String, String> parameters) throws RTIexception {
       currentSide  = Integer.parseInt(parameters.get("BridgeSide"));
       bridgeIsFree = true;
    }

    public void receiveStopQueue(HashMap<String, String> parameters) throws RTIexception {
        bridgeIsFree = false;
    }

    public void receiveCars(HashMap<String, String> parameters) throws RTIexception {
        recivedCars = makeCarsViewModel(parameters.get("carIds"), parameters.get("directionIds"));
    }

    public void sentCarIdThatCanStart(Integer carId) throws RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);
        byte[] carIdBytes = encoderFactory.createHLAASCIIstring(carId.toString()).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("youCanDriveThrough").getInteraction(),"CarId"),carIdBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("youCanDriveThrough").getInteraction(),parameters));
    }

    public void sentResetLastSpeed() throws RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("resetLastSpeed").getInteraction(),parameters));
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new QueueFederateAmbassador(this);
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
            new QueueFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }

    public ArrayList<CarViewModel> makeCarsViewModel(String carIdsString, String directionIdsString)
    {
        ArrayList<CarViewModel> receivedCars = new ArrayList<>();

        String[] carsIds = carIdsString.split(",");
        String[] directionIds = directionIdsString.split(",");

        for(int i = 0; i < carsIds.length; i++) {
            receivedCars.add(new CarViewModel(Integer.parseInt(carsIds[i].trim()), Integer.parseInt(directionIds[i].trim())));
        }
        return receivedCars;
    }
}
