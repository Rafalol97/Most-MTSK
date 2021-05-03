import Federates.Car.CarFederate;

import static java.lang.Thread.sleep;

public class SimulationStarter {
    //treat this file as starting point for more complicated simulation(you can run each federate alone or make here configuration
    public static void main(String[] args) {
        String[] federateArgs = new String[300];
        Thread initializer;
        for (int i = 0; i < 10; i++) {//upgrade it works but probably creates many bugs
            federateArgs[0] = "Client federate " + i;
            initializer = new Thread(() -> new CarFederate().main(federateArgs));
            initializer.start();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
