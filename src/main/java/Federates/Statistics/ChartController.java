package Federates.Statistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChartController implements Initializable {


    @FXML
    private BarChart<?, ?> bc;

    @FXML
    private PieChart pc;

    @FXML
    private NumberAxis x;

    @FXML
    private CategoryAxis y;

    @FXML
    private LineChart<?, ?> lc;

    @FXML
    private NumberAxis lcx;

    @FXML
    private CategoryAxis lcy;

    @FXML
    private BarChart<?, ?> startedbc;

    @FXML
    private NumberAxis startedx;

    @FXML
    private CategoryAxis startedy;

    public ChartController()
    {

    }

    public void QueueSizeBarChart(){
        ArrayList<Integer> queue1Size = new ArrayList<>();  //StatisticsFederate.Queue1Size;
        ArrayList<Integer> queue2Size = new ArrayList<>();  //StatisticsFederate.Queue2Size;
        XYChart.Series set1 = new XYChart.Series<>();
        XYChart.Series set2 = new XYChart.Series<>();
        ArrayList<Object> data1 = new ArrayList<>();
        ArrayList<Object> data2 = new ArrayList<>();
        for(int i = 0;i<queue1Size.size();i++) {
            data1.add(new XYChart.Data(Integer.toString(i),queue1Size.get(i)));
            data2.add(new XYChart.Data(Integer.toString(i),queue2Size.get(i)));
        }
        set1.getData().addAll(data1);
        set2.getData().addAll(data2);
        set1.setName("Queue 1");
        set2.setName("Queue 2");
        bc.getData().addAll(set1);
        bc.getData().addAll(set2);
    }

    public void OverallQueueSizePieChart(){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Queue 1", 1),//StatisticsFederate.OverallQueue1Size),
                new PieChart.Data("Queue 2", 1)//StatisticsFederate.OverallQueue2Size)
        );
        pc.setData(pieChartData);
        pc.setStartAngle(180);

    }

    public void GeneratedCarsLineChart(){
        ArrayList<Integer> queue1Size =  new ArrayList<>();  //StatisticsFederate.GeneratedCars;
        XYChart.Series set1 = new XYChart.Series<>();
        ArrayList<Object> data1 = new ArrayList<>();
        for(int i = 0;i<queue1Size.size();i++) {
            data1.add(new XYChart.Data(Integer.toString(i),queue1Size.get(i)));
        }
        set1.getData().addAll(data1);
        set1.setName("Number of cars");
        lc.getData().addAll(set1);
    }

    public void StartedCarsBarChart(){
        ArrayList<Integer> lightsTimer =  new ArrayList<>();  //StatisticsFederate.LightsTimer;
        ArrayList<Integer> startedCarsSize =  new ArrayList<>();  //StatisticsFederate.StartedCarsSize;
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
        startedbc.getData().addAll(set1);
        startedbc.getData().addAll(set2);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QueueSizeBarChart();
        OverallQueueSizePieChart();
        GeneratedCarsLineChart();
        StartedCarsBarChart();
    }
}
