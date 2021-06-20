package Federates.GUI;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GUIController  implements Initializable {

    @FXML
    private AnchorPane anchorStat;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void reload(Event event) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("Chart.fxml"));
            anchorStat.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
