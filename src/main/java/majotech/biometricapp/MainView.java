package majotech.biometricapp;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Model.Cliente;
import majotech.biometricapp.Util.Util;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author JoaquinGA
 */
public class MainView implements Initializable {

    private List<Cliente> clienteList = new ArrayList<>();
    @FXML
    private TableView<Cliente> tableClientes;
    @FXML
    private TableColumn<Cliente, Integer> num_Cliente;
    @FXML
    private TableColumn<Cliente, Integer> sucursal;
    @FXML
    private TableColumn<Cliente, String> nombre;
    @FXML
    private TableColumn<Cliente, String> curp;
    @FXML
    private TableColumn<Cliente, String> telefono;
    @FXML
    private TableColumn<Cliente, String> sexo;
    @FXML
    private TableColumn<Cliente, String> pais;
    @FXML
    private TableColumn<Cliente, String> estado;
    @FXML
    private TableColumn<Cliente, String> municipio;
    @FXML
    private TableColumn<Cliente, String> colonia;
    @FXML
    private TableColumn<Cliente, String> direccion;
    @FXML
    private TableColumn<Cliente, Boolean> Moroso;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadClientesFromDatabase();
        num_Cliente.setCellValueFactory(new PropertyValueFactory<>("numCliente"));
        sucursal.setCellValueFactory(new PropertyValueFactory<>("idSucursal"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        curp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        sexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        pais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        municipio.setCellValueFactory(new PropertyValueFactory<>("municipio"));
        colonia.setCellValueFactory(new PropertyValueFactory<>("colonia"));
        direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        Moroso.setCellValueFactory(new PropertyValueFactory<>("moroso"));

        tableClientes.getItems().addAll(clienteList);
    }

    private void loadClientesFromDatabase() {
        // Aquí deberías tener tu conexión a la base de datos ya creada
        Conexion connection = new Conexion();

        // Consulta SQL para obtener los datos de la tabla clientes
        String query = "SELECT * FROM clientes";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(resultSet.getInt("id_cliente"));
                cliente.setNumCliente(resultSet.getInt("num_cliente"));
                cliente.setIdSucursal(resultSet.getInt("id_sucursal"));
                cliente.setCurp(resultSet.getString("curp"));
                cliente.setNombre(resultSet.getString("nombre"));
                cliente.setTelefono(resultSet.getString("telefono"));
                cliente.setSexo(resultSet.getString("sexo"));
                cliente.setPais(resultSet.getString("pais"));
                cliente.setEstado(resultSet.getString("estado"));
                cliente.setMunicipio(resultSet.getString("municipio"));
                cliente.setColonia(resultSet.getString("colonia"));
                cliente.setDireccion(resultSet.getString("direccion"));
                cliente.setMoroso(resultSet.getBoolean("moroso"));

                clienteList.add(cliente);
            }

        } catch (SQLException e) {
            Util.showAlert("An error occurred while retrieving data from the database.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void BuscarCliente(ActionEvent event) {
        prueba();
        loadScreen("BuscarCliente.fxml");
    }   

    @FXML
    private void AgregarCliente(ActionEvent event) {
          loadScreen("AgregarCliente.fxml");
    }

    @FXML
    private void EditarCliente(ActionEvent event) {
         loadScreen("EditarCliente.fxml");
    }

    @FXML
    private void EliminarCliente(ActionEvent event) {
         loadScreen("EliminarCliente.fxml");
    }
    
    
    //Metodo que administra los loader de pantallas
    @FXML
    private AnchorPane contentPane;
    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainView.class.getResource(fxmlFile));
            AnchorPane screen = loader.load();

            contentPane.getChildren().setAll(screen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void prueba() {
        String pathGen = "src\\main\\java\\majotech\\biometricapp\\resources\\";
        try {
            Map<Integer, byte[]> fingerprintsMap = fetchFingerprintsFromDatabase();
            byte[] probeBytes = Files.readAllBytes(Paths.get(pathGen + "Dedo 1 prueba.bmp"));
            FingerprintImage probeImage = new FingerprintImage(probeBytes);
            FingerprintTemplate probe = new FingerprintTemplate(probeImage);

            FingerprintMatcher matcher = new FingerprintMatcher(probe);

            double threshold = 40;

            for (Map.Entry<Integer, byte[]> entry : fingerprintsMap.entrySet()) {
                int id = entry.getKey();
                byte[] candidateBytes = entry.getValue();

                FingerprintImage candidateImage = new FingerprintImage(candidateBytes);
                FingerprintTemplate candidate = new FingerprintTemplate(candidateImage);

                double similarity = matcher.match(candidate);

                if (similarity >= threshold) {
                    System.out.println("Las huellas coinciden para el ID: " + id);

                    // Seleccionar el registro en la tabla correspondiente al ID
                    for (Cliente cliente : clienteList) {
                        if (cliente.getIdCliente() == id) {
                            tableClientes.getSelectionModel().select(cliente);
                            break;
                        }
                    }
                } else {
                    System.out.println("Las huellas no coinciden para el ID: " + id);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Map<Integer, byte[]> fetchFingerprintsFromDatabase() {
        Map<Integer, byte[]> fingerprintsMap = new HashMap<>();
        Conexion connection = new Conexion();
        String query = "SELECT id_cliente, Huella FROM clientes";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_cliente");
                    byte[] fingerprintBytes = resultSet.getBytes("Huella");
                    fingerprintsMap.put(id, fingerprintBytes);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fingerprintsMap;
    }
}
