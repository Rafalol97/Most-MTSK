import Federates.Bridge.BridgeFederate;
import Federates.Car.CarFederate;

import static java.lang.Thread.sleep;

public class SimulationStarter {
    //treat this file as starting point for more complicated simulation(you can run each federate alone or make here configuration
    public static void main(String[] args) {
        String[] federateArgs = new String[300];
        Thread initializer;
        initializer = new Thread(() -> BridgeFederate.main(new String[0]));
        initializer.start();
    }
}
