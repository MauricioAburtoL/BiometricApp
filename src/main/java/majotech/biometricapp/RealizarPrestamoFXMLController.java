/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package majotech.biometricapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class RealizarPrestamoFXMLController implements Initializable {

    @FXML
    private TextField TFEstado;
    @FXML
    private TextField TFColonia;
    @FXML
    private TextField TFCurp;
    @FXML
    private TextField TFNombre;
    @FXML
    private TextField TFTelefono;
    @FXML
    private TextField TFPais;
    @FXML
    private TextField TFMunicipio;
    @FXML
    private TextField TFDireccion;
    @FXML
    private ImageView dedo;
    @FXML
    private Label tfStatus;
    @FXML
    private TextField TFSexo;
    @FXML
    private TextField TFCantidadPrestamo;
    @FXML
    private ComboBox<?> CBIntereses;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void openSensor(ActionEvent event) {
    }

    @FXML
    private void closeSensor(ActionEvent event) {
    }

    @FXML
    private void Guardarcliente(ActionEvent event) {
    }

    @FXML
    private void Cancelar(ActionEvent event) {
    }
    
}
