package Federates.GUI;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController  implements Initializable {


    @FXML
    public BarChart<?, ?> bc;

    @FXML
    public PieChart pc;

    @FXML
    private NumberAxis x;

    @FXML
    private CategoryAxis y;

    @FXML
    public LineChart<?, ?> lc;

    @FXML
    private NumberAxis lcx;

    @FXML
    private CategoryAxis lcy;

    @FXML
    public BarChart<?, ?> startedbc;

    @FXML
    private NumberAxis startedx;

    @FXML
    private CategoryAxis startedy;


    @FXML
    public Pane bridge;

    @FXML
    private AnchorPane anchorStat;

    @FXML
    private AnchorPane anchorSim;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void reload(Event event) {
    }
}
