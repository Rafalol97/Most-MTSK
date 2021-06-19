package Federates.GUI;

import Federates.BaseFederate;
import Federates.BaseFederateAmbassador;
import Utils.InteractionToBeSend;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.exceptions.RTIexception;
import models.Car;
import models.CarViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class GUIFederate extends BaseFederate{


    public void initializeFederate(){
        this.federateType = "QueueFederateType";
    }

    public GUIFederate() throws RTIexception {
        super();
    }

    @Override
    protected void toDoInEachIteration() throws RTIexception {

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {

    }

    @Override
    protected BaseFederateAmbassador newFedAmb(){
        return new GUIFederateAmbassador(this);
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
            new GUIFederate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            rtie.printStackTrace();
        }
    }
}
