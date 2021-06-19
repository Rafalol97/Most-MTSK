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


public class CarFederate extends BaseFederate{
    public Random randomizer = new Random();

    public ArrayList<Car> cars = new ArrayList<>();
    public ArrayList<Integer> startedCarsIds = new ArrayList<>();

    public ArrayList<Car> carsToSend = new ArrayList<>();

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
            ArrayList<String> carsToSendStrings = makeStrings(carsToSend);
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

            sentIEnteredTheBrigde(); //wyslij komunikat do mostu ze wjechalem na moscik
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
                    sentILeftTheBrigde(); //wyslij do mostu ze skonczylem
                    tmpCar.setFinished(true);
                    tmpCar.setFinishedTime(this.fedamb.getFederateTime());
                    startedCarsIds.remove(i);
                    i--;
                }
            }
            //TODO ZAKTUALIZUJ AKTUALNE POZYCJE AUT W GUI
        }
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addPublication("HLAinteractionRoot.CarCalls.WeWantToDriveThrough", "weWantToDriveThrough");
        addPublication("HLAinteractionRoot.CarCalls.IEnteredTheBridge", "iEnteredTheBridge");
        addPublication("HLAinteractionRoot.CarCalls.ILeftTheBridge", "iLeftTheBridge");

        addSubscription("HLAinteractionRoot.QueueCalls.YouCanDriveThrough","youCanDriveThrough");
        addSubscription("HLAinteractionRoot.QueueCalls.ResetLastSpeed","resetLastSpeed");
    }

    public void carWithIdCanGo(HashMap<String, String> parameters) throws RTIexception {
        logMe("ODBIERAM SAMOCHOD "+Integer.parseInt(parameters.get("CarId")));
        carToRun = Integer.parseInt(parameters.get("CarId"));
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

    private void sentIEnteredTheBrigde() throws  RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("iEnteredTheBridge").getInteraction(),parameters));
    }

    private void sentILeftTheBrigde() throws  RTIexception {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("iLeftTheBridge").getInteraction(),parameters));
    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new CarFederateAmbassador(this);
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
            new CarFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
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
        String tmpIdsListString = tmpIdsList.toString();
        String tmpSidesListString = tmpSidesList.toString();

        carsToSend.add(tmpIdsListString.substring(1, tmpIdsListString.length() - 1));
        carsToSend.add(tmpSidesListString.substring(1, tmpSidesListString.length() - 1));
        return carsToSend;
    }

}
