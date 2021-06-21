package Federates.Statistics;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Utils.InteractionToBeSend;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


public class StatisticsFederate extends BaseFederate {


    public void initializeFederate(){
        this.federateType = "StatisticsFederateType";
    }


    public ArrayList<Integer> StartedCarsSize = new ArrayList<>();
    public Integer OverallQueue1Size = 0;
    public Integer OverallQueue2Size = 0;
    public ArrayList<Integer> Queue1Size = new ArrayList<>();
    public ArrayList<Integer> Queue2Size = new ArrayList<>();
    public ArrayList<Integer> LightsTimer = new ArrayList<>();
    public ArrayList<Integer> BridgeSide = new ArrayList<>();
    public ArrayList<Integer> GeneratedCars = new ArrayList<>();

    private Integer currentGeneratedNumberOfCars = 0;

    public Integer tmpStartedCarSize;
    public Integer tmpQueue1Size;
    public Integer tmpQueue2Size;
    public Integer tmpLightsTimer;
    public Integer tmpBridgeSide;

    public StatisticsFederate() throws RTIexception {
        super();
    }

    @Override
    protected void iterationSequence() throws RTIexception {
        if(this.fedamb.getFederateTime() > 2.0) {
            StartedCarsSize.add(tmpStartedCarSize);
            Queue1Size.add(tmpQueue1Size);
            Queue2Size.add(tmpQueue2Size);
            LightsTimer.add(tmpLightsTimer);
            BridgeSide.add(tmpBridgeSide);
            GeneratedCars.add(currentGeneratedNumberOfCars);
        }
        SendStats(
                makeString(StartedCarsSize),
                OverallQueue1Size.toString(),
                OverallQueue2Size.toString(),
                makeString(Queue1Size),
                makeString(Queue2Size),
                makeString(LightsTimer),
                makeString(BridgeSide),
                makeString(GeneratedCars)
        );
    }

    private void SendStats(String StartedCarsSize, String OverallQueue1Size, String OverallQueue2Size, String Queue1Size, String Queue2Size, String LightsTimer, String BridgeSide, String GeneratedCars) throws RTIexception{
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(8);
        byte[] startedCarsSizeBytes = encoderFactory.createHLAASCIIstring(StartedCarsSize).toByteArray();
        byte[] overallQueue1SizeBytes = encoderFactory.createHLAASCIIstring(OverallQueue1Size).toByteArray();
        byte[] overallQueue2SizeBytes = encoderFactory.createHLAASCIIstring(OverallQueue2Size).toByteArray();
        byte[] queue1SizeBytes = encoderFactory.createHLAASCIIstring(Queue1Size).toByteArray();
        byte[] queue2SizeBytes = encoderFactory.createHLAASCIIstring(Queue2Size).toByteArray();
        byte[] lightsTimerBytes = encoderFactory.createHLAASCIIstring(LightsTimer).toByteArray();
        byte[] bridgeSideBytes = encoderFactory.createHLAASCIIstring(BridgeSide).toByteArray();
        byte[] generatedCarsBytes = encoderFactory.createHLAASCIIstring(GeneratedCars).toByteArray();
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"StartedCarsSize"),startedCarsSizeBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"OverallQueue1Size"),overallQueue1SizeBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"OverallQueue2Size"),overallQueue2SizeBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"Queue1Size"),queue1SizeBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"Queue2Size"),queue2SizeBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"LightsTimer"),lightsTimerBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"BridgeSide"),bridgeSideBytes);
        parameters.put(rtiamb.getParameterHandle(getInteractionClassHandle("sendStats").getInteraction(),"GeneratedCars"),generatedCarsBytes);
        interactionsToSend.add(new InteractionToBeSend(getInteractionClassHandle("sendStats").getInteraction(),parameters));
    }

    public void receiveCarData(HashMap<String, String> parameters) throws RTIexception {
        String startedCarIds = parameters.get("carIds");
        tmpStartedCarSize = startedCarIds.split(",").length;
    }

    public void receiveQueueData(HashMap<String, String> parameters) throws RTIexception {
        tmpQueue1Size=Integer.parseInt(parameters.get("Queue1Size"));
        tmpQueue2Size=Integer.parseInt(parameters.get("Queue2Size"));
    }

    public void receiveBridgeData(HashMap<String, String> parameters) throws RTIexception {
        tmpBridgeSide = Integer.parseInt(parameters.get("BridgeSide"));
        int lightsValue = Integer.parseInt(parameters.get("LightsTimer"));
        if(lightsValue<0)
            tmpLightsTimer = -1;
        else
            tmpLightsTimer = 0;
    }

    public void receiveWeWantToDriveThrough(HashMap<String, String> parameters) {
        ArrayList<CarViewModel> newCars = CarViewModel.makeCarsViewModel(parameters.get("carIds"), parameters.get("directionIds"));
        for (CarViewModel car : newCars) {
           if(car.getSide()==0){
               OverallQueue1Size++;
           }
           else OverallQueue2Size++;
        }
        currentGeneratedNumberOfCars += newCars.size();
    }
    @Override
    protected BaseFederateAmbassador returnNewFederateAmbassador(){
        return new StatisticsFederateAmbassador(this);
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
            new StatisticsFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }

    public String makeString(ArrayList<Integer> tmpArray)
    {
        ArrayList<Integer> arrayList = tmpArray;
        return arrayList.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    protected void lastActionBeforeDestroy() throws RTIexception {
        Integer queue1Sum = 0;
        Integer queue2Sum = 0;

        for (int i = 0; i<Queue1Size.size(); i++) {
            queue1Sum += Queue1Size.get(i);
            queue2Sum += Queue2Size.get(i);
        }

        System.out.println("==========================================================");
        System.out.println("======================Wyniki==============================");
        System.out.println("Srednia długość kolejki 1: " + queue1Sum / Queue1Size.size());
        System.out.println("Srednia długość kolejki 2: " + queue2Sum / Queue2Size.size());
        System.out.println("==========================================================");
    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        addSubscription("HLAinteractionRoot.Car.SendCarData", "sendCarData");
        addSubscription("HLAinteractionRoot.Queue.SendQueueData", "sendQueueData");
        addSubscription("HLAinteractionRoot.Bridge.SendBridgeData", "sendBridgeData");
        addSubscription("HLAinteractionRoot.Car.WeWantToDriveThrough", "weWantToDriveThrough");

        addPublication("HLAinteractionRoot.Statistics.SendStats", "sendStats");
    }

}
