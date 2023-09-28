package majotech.biometricapp;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Model.Cliente;
import majotech.biometricapp.Util.InitializableController;
import majotech.biometricapp.Util.Util;

public class EditarClienteFXMLController implements Initializable, InitializableController {

    Cliente c;
    @FXML
    private ChoiceBox<String> CBSexo;
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
    private TextField TFMoroso;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CBSexo.getItems().addAll("Hombre", "Mujer");
    }

    @Override
    public void initData(Object data) {
        this.c = (Cliente) data;
        System.out.println(c.toString());
        if (c != null) {
            this.CBSexo.setValue(c.getSexo());
            this.TFEstado.setText(c.getEstado());
            this.TFColonia.setText(c.getColonia());
            this.TFCurp.setText(c.getCurp());
            this.TFNombre.setText(c.getNombre());
            this.TFTelefono.setText(c.getTelefono());
            this.TFPais.setText(c.getPais());
            this.TFMunicipio.setText(c.getMunicipio());
            this.TFDireccion.setText(c.getDireccion());
            this.TFMoroso.setText(c.getMoroso().toString());
        }
    }

    @FXML
    private void Guardarcliente(ActionEvent event) {
        Conexion connection = new Conexion();
        String sexo = CBSexo.getValue();
        String estado = TFEstado.getText();
        String colonia = TFColonia.getText();
        String nombre = TFNombre.getText();
        String telefono = TFTelefono.getText();
        String pais = TFPais.getText();
        String municipio = TFMunicipio.getText();
        String direccion = TFDireccion.getText();
        String curp = TFCurp.getText();

        String sql = "UPDATE clientes SET curp = ?, nombre = ?, telefono = ?, sexo = ?, pais = ?, estado = ?, municipio = ?, colonia = ?, direccion = ? WHERE id_cliente = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (statement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return;
            }
            statement.setString(1, curp);
            statement.setString(2, nombre);
            statement.setString(3, telefono);
            statement.setString(4, sexo);
            statement.setString(5, pais);
            statement.setString(6, estado);
            statement.setString(7, municipio);
            statement.setString(8, colonia);
            statement.setString(9, direccion);
            statement.setInt(10, c.getIdCliente());

            statement.executeUpdate();
            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Actualizacion exitosa", "ActualizacionExitosa", Duration.seconds(3));
                Util.closeCurrentWindow(((Node) event.getSource()));
            } else {
                Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Actualizacion Fallida", "Ah ocurrido un error en la actualizacion", Duration.seconds(3));
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditarClienteFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void Cancelar(ActionEvent event) {
        Util.closeCurrentWindow(((Node) event.getSource()));
    }

}
