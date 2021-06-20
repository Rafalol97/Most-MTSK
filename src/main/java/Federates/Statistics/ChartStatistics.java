package Federates.Statistics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChartStatistics extends Application {
    public static ChartController controller;
    public static Parent root;
    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Chart.fxml"));
        root = (Parent) fxmlLoader.load();
        controller = fxmlLoader.<ChartController>getController();
        stage = primaryStage;
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }
}
