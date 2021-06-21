package Federates.Car;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Utils.Constants;
import Utils.InteractionToBeSend;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;
import models.Car;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;


public class CarFederate extends BaseFederate{
    public Random randomizer = new Random();

    public ArrayList<Car> cars = new ArrayList<>();
    public ArrayList<Integer> startedCarsIds = new ArrayList<>();
    public ArrayList<String> carsToSendStrings;
    double nextTimeToGenerateCars = 0.0;
    int howManyCarsToGenerate;
    int nextCarId = 0;

    int carToRun = -1;
    double lastCarSpeed = 0.0;

    public void initializeFederate(){
        this.federateType = "CarFederateType";
    }

    public CarFederate()
    {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {
        //GENERATE CARS
        if(this.fedamb.getFederateTime() > this.nextTimeToGenerateCars)
        {
            ArrayList<CarViewModel> carsToSend = new ArrayList<>();
            this.nextTimeToGenerateCars += Constants.minAddTime + (Constants.maxAddTime- Constants.minAddTime) * randomizer.nextDouble();
            this.howManyCarsToGenerate = randomizer.nextInt(Constants.maxCarsToGenerate - Constants.minCarsToGenerate) + Constants.minCarsToGenerate;
            logMe("AUTA: generuje " + howManyCarsToGenerate + " auta, nast generuje przy: " + nextTimeToGenerateCars);
            for(int i = 0; i < howManyCarsToGenerate; i++)
            {
                Car tmpCar = new Car(nextCarId, randomizer);
                cars.add(tmpCar);
                carsToSend.add(new CarViewModel(tmpCar.getId(), tmpCar.getSide()));
                nextCarId++;
            }
            carsToSendStrings = makeStrings(carsToSend);
            sentCarsToQueue(carsToSendStrings); //wyslij do kolejki Id nowych aut i ich strony
        }
        if(carToRun != -1)
        {
            logMe("AUTA: startuje auto nr: " + carToRun);
            Car tmpCar = cars.get(carToRun);

            if(tmpCar.getSpeed() < lastCarSpeed || lastCarSpeed == 0.0)
            {
                lastCarSpeed = tmpCar.getSpeed();
            }
            else
            {
                tmpCar.setSpeed(lastCarSpeed);
            }

            sendIEnteredTheBridge(); //wyslij komunikat do mostu ze wjechalem na moscik
            startedCarsIds.add(tmpCar.getId());
            tmpCar.setStarted(true);
            tmpCar.setStartedTime(this.fedamb.getFederateTime());
            carToRun = -1;
        }
        if(startedCarsIds.size() != 0)
        {
            for (int i = 0; i < startedCarsIds.size(); i++)
            {
                int id = startedCarsIds.get(i);
                logMe("AUTA: jedzie auto nr: " + id);
                Car tmpCar = cars.get(id);
                tmpCar.setCurrentState(tmpCar.getCurrentState() + tmpCar.getSpeed());
                if(tmpCar.getCurrentState() > Constants.bridgeLenght)
                {
                    logMe("AUTA: skonczylem auto nr: " + id);
                    sendILeftTheBridge(); //wyslij do mostu ze skonczylem
                    tmpCar.setFinished(true);
                    tmpCar.setFinishedTime(this.fedamb.getFederateTime());
                    startedCarsIds.remove(i);
                    i--;
                }
            }

        }
        sendCarData(Car.makeFullCarStrings(startedCarsIds,cars));
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addPublication("HLAinteractionRoot.CarCalls.WeWantToDriveThrough", "weWantToDriveThrough");
        addPublication("HLAinteractionRoot.CarCalls.IEnteredTheBridge", "iEnteredTheBridge");
        addPublication("HLAinteractionRoot.CarCalls.ILeftTheBridge", "iLeftTheBridge");

        addPublication("HLAinteractionRoot.CarCalls.SendCarData", "sendCarData");

        addSubscription("HLAinteractionRoot.QueueCalls.YouCanDriveThrough","youCanDriveThrough");
        addSubscription("HLAinteractionRoot.QueueCalls.ResetLastSpeed","resetLastSpeed");
    }

    public void carWithIdCanGo(HashMap<String, String> parameters) throws RTIexception {
        carToRun = Integer.parseInt(parameters.get("CarId"));
        logMe("ODBIERAM SAMOCHOD "+carToRun);
    }

    public void resetDirection(HashMap<String, String> parameters) throws RTIexception {
        lastCarSpeed = 0.0;
    }

    private void sentCarsToQueue(ArrayList<String> carsToSend) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(2);
        byte[] carsIdsBytes = encoderFactory.createHLAASCIIstring(carsToSend.get(0)).toByteArray();
        byte[] sideIdsBytes = encoderFactory.createHLAASCIIstring(carsToSend.get(1)).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("weWantToDriveThrough").getInteraction(),"carIds"),carsIdsBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("weWantToDriveThrough").getInteraction(),"directionIds"),sideIdsBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("weWantToDriveThrough").getInteraction(),parameters));
    }

    private void sendIEnteredTheBridge() throws  RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("iEnteredTheBridge").getInteraction(),parameters));
    }

    private void sendILeftTheBridge() throws  RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("iLeftTheBridge").getInteraction(),parameters));
    }

    private void sendCarData(ArrayList<String> startedCars) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(4);
        byte[] carsIdsBytes = encoderFactory.createHLAASCIIstring(startedCars.get(0)).toByteArray();
        byte[] carSpeeds = encoderFactory.createHLAASCIIstring(startedCars.get(1)).toByteArray();
        byte[] carCurrentStates = encoderFactory.createHLAASCIIstring(startedCars.get(2)).toByteArray();
        byte[] carSides = encoderFactory.createHLAASCIIstring(startedCars.get(3)).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendCarData").getInteraction(),"carIds"),carsIdsBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendCarData").getInteraction(),"carSpeeds"),carSpeeds);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendCarData").getInteraction(),"carCurrentStates"),carCurrentStates);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendCarData").getInteraction(),"carSides"),carSides);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("sendCarData").getInteraction(),parameters));
    }

    public ArrayList<String> makeStrings(ArrayList<CarViewModel> cars)
    {
        ArrayList<String> carsToSend = new ArrayList<>();
        ArrayList<Integer> tmpIdsList = new ArrayList<>();
        ArrayList<Integer> tmpSidesList = new ArrayList<>();
        for (CarViewModel car: cars)
        {
            tmpIdsList.add(car.getId());
            tmpSidesList.add(car.getSide());
        }
        carsToSend.add(tmpIdsList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        carsToSend.add(tmpSidesList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return carsToSend;
    }



    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new CarFederateAmbassador(this);
    }

    public static void main( String[] args )
    {
        String federateName = "exampleFederate";
        if( args.length != 0 )
        {
            federateName = args[0];
        }
        try
        {
            new CarFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }
}
