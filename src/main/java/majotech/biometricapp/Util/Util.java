package majotech.biometricapp.Util;

import java.io.FileInputStream;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Util {

    public static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void closeCurrentWindow(Node source) {
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static void showAlertWithAutoClose(Alert.AlertType alertType, String title, String headerText, Duration duration) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText("Este mensaje se cerra en automatico");
        Timeline timeline = new Timeline(new KeyFrame(duration, ae -> alert.close()));
        timeline.setCycleCount(1);
        timeline.play();

        alert.showAndWait();
    }

    public static byte[] obtenerDatosBMP(String name) throws IOException {
        String rutaArchivoBMP = "src\\main\\java\\majotech\\biometricapp\\resources\\" + name + ".bmp";
        try (FileInputStream fis = new FileInputStream(rutaArchivoBMP)) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            return buffer;
        }
    }

    public static void openView(String fxmlPath, String titulo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Util.class.getResource("/majotech/biometricapp/" + fxmlPath + ".fxml"));
            AnchorPane ventanaPane = fxmlLoader.load();

            Stage ventanaStage = new Stage();
            ventanaStage.initModality(Modality.APPLICATION_MODAL);
            ventanaStage.setTitle(titulo);
            ventanaStage.setScene(new Scene(ventanaPane));
            ventanaStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void openView(String fxmlPath, String titulo, Class<T> controllerClass, Object data) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Util.class.getResource("/majotech/biometricapp/" + fxmlPath + ".fxml"));
            AnchorPane ventanaPane = fxmlLoader.load();
            T controller = controllerClass.getDeclaredConstructor().newInstance();
            controller = fxmlLoader.getController();
            if (controller instanceof InitializableController) {
                ((InitializableController) controller).initData(data);
            }

            Stage ventanaStage = new Stage();
            ventanaStage.initModality(Modality.APPLICATION_MODAL);
            ventanaStage.setTitle(titulo);
            ventanaStage.setScene(new Scene(ventanaPane));
            ventanaStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
