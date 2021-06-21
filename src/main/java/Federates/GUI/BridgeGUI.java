package Federates.GUI;

import Federates.Statistics.ChartController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
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
        ArrayList<Car> cars = new ArrayList<>();
        cars.add(new Car(1,2.0,2.0,0));
        addSqToBridgePane(cars);
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
                Rectangle[] rect = new Rectangle[cars.size()];
                for (int i = 0; i < cars.size(); i++) {
                    rect[i] = addToPaneOnPosition(cars.get(i), pane);

                }
                Group root = new Group(rect);
                pane.getChildren().setAll(root);
            }
        });
    }

    private static Rectangle addToPaneOnPosition(Car car, Pane pane){
        if(car.getSide()==0){
            return new Rectangle(10, 20, pane.getWidth() * (car.getCurrentState()/(20.0)) , 20);
        }
        else{
            return new Rectangle(10, 20, pane.getWidth() - pane.getWidth() * (car.getCurrentState()/(20.0)) , 20);
        }

    }
}
