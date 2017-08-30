package pl.edu.pb.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import pl.edu.pb.algorithm.Algorithm;

import pl.edu.pb.algorithm.GRASPAlgorithm;
import pl.edu.pb.algorithm.ILSAlgorithm;
import pl.edu.pb.graph.DataReader;
import pl.edu.pb.graph.Graph;
import pl.edu.pb.graph.Point;
import pl.edu.pb.gui.drawing.DrawingVisitor;
import pl.edu.pb.gui.drawing.ResizableCanvas;
import pl.edu.pb.gui.logger.TextAreaAppender;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Created by Mateusz on 14.05.2017.
 */
public class AlgorithmController {
    public static final String GRASP = "GRASP";
    public static final String ILS = "ILS";
    private ObservableList<Point> points;
    private ObservableList<String> algs;

    DrawingVisitor visitor;
    AlgorithmRunner runner;
    Point[] allPoints;
    Point[] hotelsPoints;
    int days;
    int lengthLimit;
    int maxIterations;
    int solutionsN;

    @FXML
    private Button loadButton;
    @FXML
    private Button start;

    @FXML
    private Button stop;

    @FXML
    private TextField dataSource;

    @FXML
    private TextField iterations;

    @FXML
    private TextField daysCount;

    @FXML
    private TextField maxRouteLength;

    @FXML
    private TextField hotelsDataSource;

    @FXML
    private TextField solutionsCount;

    @FXML
    private ChoiceBox<Point> hotels;

    @FXML
    private ChoiceBox<String> algorithms;

    @FXML
    private TextArea logs;

    @FXML
    private Pane routeHolder;

    @FXML
    private CheckBox roundToFloor;

    @FXML
    private CheckBox exportSolution;

    @FXML
    private CheckBox degToKm;

    public AlgorithmController() {
        points = FXCollections.observableArrayList();
        algs = FXCollections.observableArrayList();
        algs.add(GRASP);
        algs.add(ILS);
    }

    public void initialize() {
        TextAreaAppender.setTextArea(logs);
        algorithms.setItems(algs);
        hotels.setItems(points);
        algorithms.getSelectionModel().selectFirst();
        iterations.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    iterations.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        daysCount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    daysCount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        maxRouteLength.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    maxRouteLength.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        solutionsCount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    solutionsCount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ResizableCanvas canvas = new ResizableCanvas();
        routeHolder.getChildren().add(canvas);
        canvas.widthProperty().bind(routeHolder.widthProperty());
        canvas.heightProperty().bind(routeHolder.heightProperty());
        visitor = new DrawingVisitor(canvas);
    }

    @FXML
    private void loadData() {
        DataReader reader = new DataReader();
        try {
            points.clear();
            allPoints = reader.readTxtFormat(dataSource.getText());
            hotelsPoints = reader.readTxtFormat(hotelsDataSource.getText());
            points.addAll(Arrays.asList(hotelsPoints));
            hotels.getSelectionModel().selectFirst();
            start.setDisable(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showErrorMessage(e.getLocalizedMessage());
        }
    }

    private void showErrorMessage(String localizedMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Błąd podczas parsowania danych");
        alert.setContentText(localizedMessage);
        alert.showAndWait();
    }

    @FXML
    private void startAlgorithm() {
        try {
            lengthLimit = Integer.parseInt(maxRouteLength.getText());
            days = Integer.parseInt(daysCount.getText());
            maxIterations = Integer.parseInt(iterations.getText());
            Algorithm alg = null;

            Point[] runPoints = new Point[allPoints.length + 1];
            Point hotel = hotels.getValue();
            solutionsN = Integer.parseInt(solutionsCount.getText());
            Point hotelCopy = new Point(hotel.getX(), hotel.getY(), 0, 0);
            for (int i = 0; i < allPoints.length; i++) {
                runPoints[i + 1] = allPoints[i];
            }
            runPoints[0] = hotelCopy;
                Graph.initalize(runPoints, roundToFloor.isSelected(), degToKm.isSelected());
            visitor.setPoints(runPoints);
            switch (algorithms.getValue()) {
                case GRASP:
                    alg = new GRASPAlgorithm(runPoints, 0, lengthLimit, days, maxIterations, solutionsN, visitor);
                    break;
                case ILS:
                    alg=new ILSAlgorithm(runPoints,0,lengthLimit,days,maxIterations,solutionsN,visitor);
                default:
                    break;
            }
            if (alg != null) {
                runner = new AlgorithmRunner(alg, exportSolution.isSelected());
                runner.start();
            }
            stop.setDisable(false);
            start.setDisable(true);
            loadButton.setDisable(true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showErrorMessage("Uzupełnij wszystkie pola przed uruchomieniem algorytmu");
        }
    }

    @FXML
    public void stopAlgorithm() {
        if (runner != null && runner.isAlive()) {
            runner.stop();
        }
        start.setDisable(false);
        stop.setDisable(true);
        loadButton.setDisable(false);
    }
}
