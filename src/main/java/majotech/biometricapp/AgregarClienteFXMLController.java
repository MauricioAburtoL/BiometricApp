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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class AgregarClienteFXMLController implements Initializable {

    @FXML
    private ChoiceBox<String> CBSexo;
    @FXML
    private TextField TFEstado;
    @FXML
    private TextField TFColonia;
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
    private TextField TFMoroso;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void TFCurp(ActionEvent event) {
    }


    @FXML
    private void Guardarcliente(ActionEvent event) {
    }

    @FXML
    private void Cancelar(ActionEvent event) {
    }
    
}
