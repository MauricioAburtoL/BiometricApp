/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Util;

import javafx.scene.control.Alert;

/**
 *
 * @author JoaquinGA
 */
public class Util {
    public  static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Error Alert");
        alert.setHeaderText("Ha ocurrido un Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
