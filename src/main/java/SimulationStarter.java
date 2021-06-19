import Federates.Bridge.BridgeFederate;
import Federates.Car.CarFederate;
import Federates.GUI.BridgeGUI;
import Federates.Queue.QueueFederate;


public class SimulationStarter {
    public static void main(String[] args) {
        Thread initializer;
        initializer = new Thread(() -> BridgeFederate.main(new String[]{"BridgeFederator"}));
        initializer.start();
        
        initializer = new Thread(() -> QueueFederate.main(new String[]{"QueueFederator"}));
        initializer.start();

        initializer = new Thread(() -> CarFederate.main(new String[]{"CarFederator"}));
        initializer.start();

        initializer = new Thread(() -> BridgeGUI.main(new String[]{"GUI"}));
        initializer.start();
    }
}
