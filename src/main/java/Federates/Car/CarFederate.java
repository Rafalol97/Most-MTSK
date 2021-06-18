package Federates.Car;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Federates.Queue.QueueFederateAmbassador;
import Utils.Constants;
import Utils.InteractionToBeSend;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class CarFederate extends BaseFederate{
    public Random randomizer = new Random();

    public ArrayList<Car> cars = new ArrayList<>();
    public ArrayList<Integer> startedCarsIds = new ArrayList<>();

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
            this.nextTimeToGenerateCars += Constants.minAddTime + (Constants.maxAddTime- Constants.minAddTime) * randomizer.nextDouble();
            this.howManyCarsToGenerate = randomizer.nextInt(Constants.maxCarsToGenerate - Constants.minCarsToGenerate) + Constants.minCarsToGenerate;
            for(int i = 0; i < howManyCarsToGenerate; i++)
            {
                cars.add(new Car(nextCarId, randomizer));
                //TODO wyslij do kolejki Id nowego auto i jego strone
                nextCarId++;
            }
        }
        if(carToRun != -1)
        {
            Car tmpCar = cars.get(carToRun);

            if(tmpCar.speed < lastCarSpeed || lastCarSpeed == 0)
            {
                //TODO wyslij do kolejki predkosc autka (kolejka resetuje predkosc kiedy most powie jej STOP)
            }
            else
            {
                tmpCar.speed = lastCarSpeed;
            }

            //TODO wyslij komunikat do mostu ze wjechalem na moscik
            startedCarsIds.add(tmpCar.id);
            tmpCar.started = true;
            tmpCar.startedTime = this.fedamb.getFederateTime();
            carToRun = -1;
        }
        if(startedCarsIds.size() != 0)
        {
            for (int i = 0; i < startedCarsIds.size(); i++)
            {
                int id = startedCarsIds.get(i);
                Car tmpCar = cars.get(id);
                tmpCar.currentState += tmpCar.speed;
                if(tmpCar.currentState > Constants.bridgeLenght)
                {
                    //TODO wyslij do mostu ze skonczylem
                    tmpCar.finished = true;
                    tmpCar.finishedTime = this.fedamb.getFederateTime();
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

class Car {
    int id;
    double speed;
    int side;
    double currentState = 0.0;

    boolean started = false;
    double startedTime = 0.0;

    boolean finished = false;
    double finishedTime = 0.0;

    public Car(int id, Random randomizer)
    {
        this.id = id;
        this.speed = Constants.minCarSpeed + (Constants.maxCarSpeed - Constants.minCarSpeed) * randomizer.nextDouble();
        this.side = (int)Math.round(Math.random());
    }
}
