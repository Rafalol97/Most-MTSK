import Federates.Bridge.BridgeFederate;
import Federates.Car.CarFederate;
import Federates.GUI.BridgeGUI;
import Federates.GUI.GUIFederate;
import Federates.Queue.QueueFederate;
import Federates.Statistics.StatisticsFederate;


public class SimulationStarter {
    public static void main(String[] args) {
        Thread initializer;
        initializer = new Thread(() -> BridgeFederate.main(new String[]{"BridgeFederator"}));
        initializer.start();
        
        initializer = new Thread(() -> QueueFederate.main(new String[]{"QueueFederator"}));
        initializer.start();

        initializer = new Thread(() -> CarFederate.main(new String[]{"CarFederator"}));
        initializer.start();

        initializer = new Thread(() -> StatisticsFederate.main(new String[]{"StatisticsFederate"}));
        initializer.start();

        initializer = new Thread(() -> GUIFederate.main(new String[]{"GUIFederate"}));
        initializer.start();

        BridgeGUI bridgeGUI = new BridgeGUI();
        BridgeGUI.main(new String[]{"hehe"});
    }
}
