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
    double lastCarSpeed = 0;

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
            for(int i = 0; i < howManyCarsToGenerate; i++)
            {
                Car tmpCar = new Car(nextCarId, randomizer);
                cars.add(tmpCar);
                carsToSend.add(new CarViewModel(tmpCar.getId(), tmpCar.getSide()));
                nextCarId++;
            }
            //TODO wyslij do kolejki Id nowych aut i ich strony
        }
        if(carToRun != -1)
        {
            Car tmpCar = cars.get(carToRun);

            if(tmpCar.getSpeed() < lastCarSpeed || lastCarSpeed == 0)
            {
                lastCarSpeed = tmpCar.getSpeed();
            }
            else
            {
                tmpCar.setSpeed(lastCarSpeed);
            }

            //TODO wyslij komunikat do mostu ze wjechalem na moscik
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
                Car tmpCar = cars.get(id);
                tmpCar.setCurrentState(tmpCar.getCurrentState() + tmpCar.getSpeed());
                if(tmpCar.getCurrentState() > Constants.bridgeLenght)
                {
                    //TODO wyslij do mostu ze skonczylem
                    tmpCar.setFinished(true);
                    tmpCar.setFinishedTime(this.fedamb.getFederateTime());
                    startedCarsIds.remove(i);
                    i--;
                }
            }
        }
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addPublication("HLAinteractionRoot.CarCalls.IWantToDriveThrough", "iWantToDriveThrough");
        addPublication("HLAinteractionRoot.CarCalls.IEnteredTheBridge", "iEnteredTheBridge");
        addPublication("HLAinteractionRoot.CarCalls.ILeftTheBridge", "iLeftTheBridge");

        addSubscription("HLAinteractionRoot.QueueCalls.YouCanDriveThrough","youCanDriveThrough");
    }

    public void carWithIdCanGo(HashMap<String, String> parameters) throws RTIexception {
        carToRun = Integer.parseInt(parameters.get("CarId"));
        System.out.println("Samochod = carId jazda!!!");
    }

    private void sendFreeState(Integer bridgeSide) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);
        byte[] bridgeSideBytes = encoderFactory.createHLAASCIIstring(bridgeSide.toString()).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("bridgeIsFree").getInteraction(),"BridgeSide"),bridgeSideBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("bridgeIsFree").getInteraction(),parameters));
        System.out.println("InformationSent");
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
}
