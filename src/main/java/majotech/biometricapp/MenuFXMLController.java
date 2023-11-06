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
import majotech.biometricapp.Util.InitializableController;
import majotech.biometricapp.Util.Util;
import majotech.biometricapp.AutorizarPrestamosFXMLController;
import majotech.biometricapp.Model.Usuario;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class MenuFXMLController implements Initializable,InitializableController {
    private Usuario usu;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void BTPrestamos(ActionEvent event) {
         Util.openView("AutorizarPrestamosFXML", "Pagina de prestamos", AutorizarPrestamosFXMLController.class, usu);
    }

    @FXML
    private void BTAdministracion(ActionEvent event) {
         Util.openView("MainView", "Pagina Administracion", MainView.class, usu.getIdSucursal());
    }

    @Override
    public void initData(Object data) {
        this.usu = (Usuario) data;
    }
    
}
