package majotech.biometricapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Util.Util;
import majotech.biometricapp.Util.LectorHuella;

public class AgregarClienteFXMLController implements Initializable {

    LectorHuella lc;

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
    private ImageView dedo;
    @FXML
    private TextField TFCurp;
    @FXML
    private Label tfStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lc = new LectorHuella();
        CBSexo.getItems().addAll("Hombre", "Mujer");

        TextFormatter<Integer> telFormatter = new TextFormatter<>(new IntegerStringConverter(), 0,
                c -> c.getControlNewText().matches("\\d*") ? c : null);
        TFTelefono.setTextFormatter(telFormatter);
    }

    @FXML
    private void Guardarcliente(ActionEvent event) throws SQLException, IOException {
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

        String sql = "INSERT INTO clientes (num_cliente, id_sucursal, curp, nombre, telefono, sexo, pais, estado, municipio, colonia, direccion, moroso, Huella) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (statement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return;
            }
            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.setString(3, curp);
            statement.setString(4, nombre);
            statement.setString(5, telefono);
            statement.setString(6, sexo);
            statement.setString(7, pais);
            statement.setString(8, estado);
            statement.setString(9, municipio);
            statement.setString(10, colonia);
            statement.setString(11, direccion);
            statement.setInt(12, 1);
            byte[] bmpData = Util.obtenerDatosBMP(TFCurp.getText());

            statement.setBytes(13, bmpData);
            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas != 0) {
                Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Insercion exitosa", "Se ha insertado el cliente", Duration.seconds(3));
                Util.closeCurrentWindow(((Node) event.getSource()));
            } else {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Insercion Fallida", "Reviza los campos", Duration.seconds(3));
            }
        }

    }

    @FXML
    private void Cancelar(ActionEvent event) {
        Util.closeCurrentWindow(((Node) event.getSource()));
    }

    @FXML
    private void openSensor(ActionEvent event) {
        if (TFCurp.getText().isEmpty()) {
            Util.showAlert("Campo de curp vacio", Alert.AlertType.WARNING);
            return;
        }
        tfStatus.setText("Activo");
        tfStatus.setTextFill(Color.RED);
        lc.abrirSensor(null, null, false, dedo, TFCurp, tfStatus);
        
    }

    @FXML
    private void closeSensor(ActionEvent event) {
        tfStatus.setText("Desactivado");
        tfStatus.setTextFill(Color.GREEN);
        lc.closeSensor();
    }
}
