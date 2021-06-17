package Federates.Bridge;

import Federates.BaseFederate;
import hla.rti1516e.exceptions.RTIexception;


public class BridgeFederate extends BaseFederate{

    public void initializeFederate(){
        this.federateType = "BridgeFederateType";
    }

    public BridgeFederate(){
        super();
    }

    @Override
    protected void toDoInEachIteration(){

    }

    @Override
    protected void addPublicationsAndSubscriptions() throws RTIexception {
        super.addPublicationsAndSubscriptions();
    }
}
