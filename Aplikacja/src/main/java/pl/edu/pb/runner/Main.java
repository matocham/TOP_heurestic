package pl.edu.pb.runner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.pb.gui.AlgorithmController;

import java.io.IOException;

/**
 * Created by Dominik on 2017-05-07.
 */
public class Main extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String args[]) {

        launch(args);

        //FirstAlgorithm f=new FirstAlgorithm(new DataReader().readTxtFormat("test.txt"),1,7600);
        //f.calculateSolution();


        }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zwiedzanie");
        initRootLayout();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/mainWindow.fxml"));
            rootLayout = loader.load();
            AlgorithmController controller = loader.getController();
            primaryStage.setOnCloseRequest( x-> {
                controller.stopAlgorithm();
                Platform.exit();
            });
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
