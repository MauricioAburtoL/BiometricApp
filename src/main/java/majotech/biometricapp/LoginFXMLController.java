/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package majotech.biometricapp;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Util.Util;

public class LoginFXMLController implements Initializable {

    @FXML
    private PasswordField CampoPassword;
    @FXML
    private TextField CampoCorreo;

    private int sucursal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void IniciarSession(ActionEvent event) {
        if (verificarCredenciales(CampoCorreo.getText(), CampoPassword.getText())) {
            Stage stage = (Stage) CampoCorreo.getScene().getWindow();

            // Cierra la ventana actual.
            
            Util.openView("MainView", "Principal", LoginFXMLController.class, sucursal);

        } else {
            System.out.println("No son correctas");
        }

    }

    public boolean verificarCredenciales(String nombreUsuario, String contrasena) {
        Conexion connection = new Conexion();
        String sql = "SELECT id_sucursal FROM usuarios WHERE email = ? AND pass = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (preparedStatement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return false;
            }
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contrasena);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    sucursal = result.getInt("id_sucursal");
                    return true; // Si hay un resultado, el usuario y contraseña son correctos.    
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace(); // Manejo de errores, puedes personalizarlo según tus necesidades.
        }
        return false;
    }
}
