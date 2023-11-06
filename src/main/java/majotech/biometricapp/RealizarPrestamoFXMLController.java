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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Model.Cliente;
import majotech.biometricapp.Model.Sucursal;
import majotech.biometricapp.Util.InitializableController;
import majotech.biometricapp.Util.LectorHuella;
import majotech.biometricapp.Util.Util;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class RealizarPrestamoFXMLController implements Initializable, InitializableController{
    Cliente cliente;
    LectorHuella lc;
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
    
    @Override
    public void initData(Object data) {
        this.cliente =  (Cliente) data;
        LlenarInfor(cliente);
    }
    
    private void LlenarInfor(Cliente c){
        TFNombre.setText(c.getNombre());
        TFTelefono.setText(c.getTelefono());
        TFSexo.setText(c.getSexo());
        TFCurp.setText(c.getCurp());
        TFPais.setText(c.getPais());
        TFDireccion.setText(c.getDireccion());
        TFEstado.setText(c.getEstado());
        Sucursal s = obtenerInfoSucursal(c.getIdSucursal());
        TFMunicipio.setText(s.getMunicipiio());
        TFColonia.setText(c.getColonia());
    }

    @FXML
    private void openSensor(ActionEvent event) {
        if (TFCurp.getText().isEmpty()) {
            Util.showAlert("Campo de curp vacio", Alert.AlertType.WARNING);
            return;
        }
        tfStatus.setText("Activo");
        tfStatus.setTextFill(Color.RED);
        lc.abrirSensor(null, null, true, dedo, TFCurp, tfStatus, cliente.getIdCliente());
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

    public Sucursal obtenerInfoSucursal(int idSucursal) {
        Sucursal sucursal = new Sucursal();
        Conexion connection = new Conexion();
        String sql = "SELECT * FROM sucursal WHERE id_sucursal = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (preparedStatement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return null;
            }
            preparedStatement.setInt(1, cliente.getIdSucursal());
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    sucursal.setNombre(result.getString("nombre"));
                    sucursal.setMunicipiio(result.getString("municipio"));
                    sucursal.setColonia(result.getString("colonia"));
                    return sucursal;   
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace(); // Manejo de errores, puedes personalizarlo según tus necesidades.
        }
        return null;
    }
    
    
}
