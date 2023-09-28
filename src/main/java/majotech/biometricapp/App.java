package majotech.biometricapp;

import javafx.application.Application;
import javafx.stage.Stage;
import majotech.biometricapp.Util.Util;

public class App extends Application {    
    @Override
    public void start(Stage stage){
        Util.openView("MainView", "Principal");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
