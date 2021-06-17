import Federates.Bridge.BridgeFederate;
public class SimulationStarter {
    //treat this file as starting point for more complicated simulation(you can run each federate alone or make here configuration
    public static void main(String[] args) {
        String[] federateArgs = new String[300];
        federateArgs[0] = "BridgeFederator";
        Thread initializer;
        initializer = new Thread(() -> BridgeFederate.main(federateArgs));
        initializer.start();
    }
}
