package majotech.biometricapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.layout.AnchorPane;

/**
 * JavaFX App
 */
public class App extends Application {

    
     private Stage primaryStage;
    private AnchorPane mainLayout;
    
    @Override
    public void start(Stage stage) throws IOException {
       // FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainView" + ".fxml"));
        //stage.setScene(new Scene(fxmlLoader.load()));
        // stage.show();
        
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Main Form");

        showMainView(); // Show the initial screen
    }
    
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("MainView.fxml"));
            mainLayout = loader.load();

            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}