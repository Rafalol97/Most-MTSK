import Federates.Bridge.BridgeFederate;
import Federates.Car.CarFederate;
import Federates.Queue.QueueFederate;


public class SimulationStarter {
    //treat this file as starting point for more complicated simulation(you can run each federate alone or make here configuration
    public static void main(String[] args) {
        Thread initializer;
        initializer = new Thread(() -> BridgeFederate.main(new String[]{"BridgeFederator"}));
        initializer.start();

        initializer = new Thread(() -> QueueFederate.main(new String[]{"QueueFederator"}));
        initializer.start();

        initializer = new Thread(() -> CarFederate.main(new String[]{"CarFederator"}));
        initializer.start();
    }
}
