package Federates.GUI;

import Utils.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Car;

import java.io.IOException;
import java.util.ArrayList;

public class BridgeGUI extends Application {

    public static GUIController guiController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        InnitializeMainStage(primaryStage);
    }

    private void InnitializeMainStage(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("GUI.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
        guiController = fxmlLoader.getController();
    }

    public static void addSqToBridgePane(ArrayList<Car> cars){
        Platform.runLater(()->{
            Pane pane = guiController.bridge;
            pane.getChildren().clear();
            if(cars !=null && cars.size()!=0) {
                Node[] rect = new Node[cars.size()*2];
                for (int i = 0; i < cars.size(); i++) {
                    rect[i] = addToPaneOnPosition(cars.get(i), pane);
                }
                for (int i = cars.size(); i < cars.size()*2; i++) {
                    rect[i] = addToPaneOnPositionText(cars.get(i-cars.size()), pane);
                }
                //Group root = new Group(rect);
                pane.getChildren().setAll(rect);
                /*ObservableList<Node> nodes = pane.getChildren();
                for (int i = 0; i < cars.size(); i++) {
                    while(getCurrentPosition(cars.get(i), pane) >  (nodes.get(i)).getTranslateX())
                    {
                        System.out.println("dsjaidjasdjaisodjasiodasjidjasioajsdisaiojidoas");
                        if(cars.get(i).getSide()==0) {
                            (nodes.get(i)).setTranslateX(((Rectangle)nodes.get(i)).getTranslateX() + 1);
                        }
                        else {
                            (nodes.get(i)).setTranslateX(((Rectangle)nodes.get(i)).getTranslateX() - 1);
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //pane.getChildren().clear();
                    //pane.getChildren().setAll(rect);
                }*/
            }
        });
    }

    private static double getCurrentPosition(Car car, Pane pane)
    {
        if(car.getSide()==0){
            return pane.getWidth() * (car.getCurrentState()/(Constants.bridgeLength));
        }
        else{
            return pane.getWidth() - (pane.getWidth() * (car.getCurrentState()/(Constants.bridgeLength)));
        }
    }

    private static Text addToPaneOnPositionText(Car car, Pane pane){
        if(car.getSide()==0){
            return new Text(pane.getWidth() * (car.getCurrentState()/(Constants.bridgeLength)), 20,Integer.toString(car.getId()));
        }
        else{
            return new Text( pane.getWidth() - (pane.getWidth() * (car.getCurrentState()/(Constants.bridgeLength))), (pane.getHeight()-20-40),Integer.toString(car.getId()));
        }

    }

    private static Rectangle addToPaneOnPosition(Car car, Pane pane){
        if(car.getSide()==0){
            return new Rectangle(pane.getWidth() * ((car.getCurrentState())/(Constants.bridgeLength)), 20,40,30);
        }
        else{
            return new Rectangle( pane.getWidth() - (pane.getWidth() * ((car.getCurrentState())/(Constants.bridgeLength))), (pane.getHeight()-20-40), 40  , 30);
        }

    }

    public static void updateStatistics(ArrayList<Integer> Queue1Size,ArrayList<Integer> Queue2Size,
                                        Integer OverallQueue1Size, Integer OverallQueue2Size,
                                        ArrayList<Integer> GeneratedCars,ArrayList<Integer> lightsTimer,
                                        ArrayList<Integer> startedCarsSize ){
        Platform.runLater(()-> {
            QueueSizeBarChart(Queue1Size,Queue2Size);
            StartedCarsBarChart(lightsTimer,startedCarsSize);
            OverallQueueSizePieChart(OverallQueue1Size,OverallQueue2Size);
            GeneratedCarsLineChart(GeneratedCars);
        });
    }

    public static void QueueSizeBarChart( ArrayList<Integer> Queue1Size,ArrayList<Integer> Queue2Size){
        XYChart.Series set1 = new XYChart.Series<>();
        XYChart.Series set2 = new XYChart.Series<>();
        ArrayList<Object> data1 = new ArrayList<>();
        ArrayList<Object> data2 = new ArrayList<>();
        for(int i = 0;i<Queue1Size.size();i++) {
            data1.add(new XYChart.Data(Integer.toString(i),Queue1Size.get(i)));
            data2.add(new XYChart.Data(Integer.toString(i),Queue2Size.get(i)));
        }
        set1.getData().addAll(data1);
        set2.getData().addAll(data2);
        set1.setName("Queue 1");
        set2.setName("Queue 2");
        guiController.bc.getData().clear();
        guiController.bc.getData().addAll(set1);
        guiController.bc.getData().addAll(set2);
    }

    public static void OverallQueueSizePieChart(Integer OverallQueue1Size, Integer OverallQueue2Size){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Queue 1", OverallQueue1Size),
                new PieChart.Data("Queue 2", OverallQueue2Size)
        );
        guiController.pc.getData().clear();
        guiController.pc.setData(pieChartData);
        guiController.pc.setStartAngle(180);

    }

    public static void GeneratedCarsLineChart(ArrayList<Integer> GeneratedCars){
        XYChart.Series set1 = new XYChart.Series<>();
        ArrayList<Object> data1 = new ArrayList<>();
        for(int i = 0;i<GeneratedCars.size();i++) {
            data1.add(new XYChart.Data(Integer.toString(i),GeneratedCars.get(i)));
        }
        set1.getData().addAll(data1);
        set1.setName("Number of cars");
        guiController.lc.getData().clear();
        guiController.lc.getData().addAll(set1);
    }

    public static void StartedCarsBarChart(ArrayList<Integer> lightsTimer, ArrayList<Integer> startedCarsSize){
        XYChart.Series set1 = new XYChart.Series<>();
        XYChart.Series set2 = new XYChart.Series<>();
        ArrayList<Object> data1 = new ArrayList<>();
        ArrayList<Object> data2 = new ArrayList<>();
        int iterations;
        if(startedCarsSize.size()>=lightsTimer.size()){
            iterations = lightsTimer.size();
        }
        else {
            iterations = startedCarsSize.size();
        }
        for(int i = 0;i<iterations;i++) {
            data1.add(new XYChart.Data(Integer.toString(i),lightsTimer.get(i)));
            data2.add(new XYChart.Data(Integer.toString(i),startedCarsSize.get(i)));
        }
        set1.getData().addAll(data1);
        set2.getData().addAll(data2);
        set1.setName("Red light");
        set2.setName("Started Cars");
        guiController.startedbc.getData().clear();
        guiController.startedbc.getData().addAll(set1);
        guiController.startedbc.getData().addAll(set2);
    }

    public static void UpdateQueueData(Integer Queue1Size, Integer Queue2Size){
        Platform.runLater(()->{
            guiController.CarsInQueue1.setText( Queue1Size.toString());
            guiController.CarsInQueue2.setText( Queue2Size.toString());
        });
    }
}
