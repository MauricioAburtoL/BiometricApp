package majotech.biometricapp;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Model.Cliente;
import majotech.biometricapp.Util.InitializableController;
import majotech.biometricapp.Util.Util;
import majotech.biometricapp.Util.LectorHuella;

public class MainView implements Initializable, InitializableController {

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
    @FXML
    private TextField tfBuscar;

    private int sucursalB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    }

    @Override
    public void initData(Object data) {
        this.sucursalB = (int) data;
        actualizarTablaClientes();
    }

    @FXML
    private void BuscarCliente(ActionEvent event) {
        tableClientes.getSelectionModel().clearSelection();
        LectorHuella lc = new LectorHuella();
        lc.abrirSensor(clienteList, tableClientes, true, null, null);

    }

    @FXML
    private void AgregarCliente(ActionEvent event) {
        Util.openView("AgregarClienteFXML", "Agregar Cliente");
        actualizarTablaClientes();
    }

    @FXML
    private void EditarCliente(ActionEvent event) {
        Cliente clienteSeleccionado = obtenerClienteSeleccionado();
        if (clienteSeleccionado != null) {
            Util.openView("EditarClienteFXML", "Editar Cliente", EditarClienteFXMLController.class, clienteSeleccionado);
            actualizarTablaClientes();
        } else {
            Util.showAlertWithAutoClose(Alert.AlertType.WARNING, "Advertencia", "Se debe de seleccion un cliente para editar", Duration.seconds(3));
        }
    }

    @FXML
    private void EliminarCliente(ActionEvent event) throws SQLException, IOException {
        Conexion connection = new Conexion();

        Cliente numClienteSeleccionado = obtenerClienteSeleccionado();

        if (numClienteSeleccionado != null) {
            String sql = "DELETE FROM clientes WHERE curp = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, numClienteSeleccionado.getCurp());
                int filasafectadas = statement.executeUpdate();
                if (filasafectadas != 0) {
                    Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Eliminacion Exitosa", "La Eliminacion fue Exitosa", Duration.seconds(3));
                } else {

                }
            }
        } else {
            Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Eliminacion Fallida", "No se ha podido seleccionado ningun cliente", Duration.seconds(3));
        }
    }

    @FXML
    private void ActualizarTabla(ActionEvent event) {
        actualizarTablaClientes();
    }

    public void actualizarTablaClientes() {
        clienteList.clear();

        loadClientesFromDatabase();

        tableClientes.getItems().clear();
        tableClientes.getItems().addAll(clienteList);
    }

    private void loadClientesFromDatabase() {
        Conexion connection = new Conexion();
        String query = "SELECT * FROM clientes";
        if (sucursalB != 1) {
            query += " WHERE id_sucursal = ?";
        }
        System.out.println(query);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (preparedStatement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return;
            }
            if (sucursalB != 1) {
                preparedStatement.setInt(1, sucursalB);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            Util.showAlert("Ah ocurrido un error en la base de datos al obtener los datos de los clientes" + e,
                    Alert.AlertType.ERROR);
        }
    }

    private void reloadTable(Cliente c) {
        List<Cliente> clienteList2 = new ArrayList<>();
        if (c != null) {
            clienteList2.add(c);
        }
        for (Cliente cliente : clienteList) {
            if (!c.equals(c)) {
                clienteList2.add(cliente);
            }
        }
        clienteList.clear();
        clienteList = clienteList2;
    }
    private Cliente obtenerClienteSeleccionado() {
        Cliente clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();
        return clienteSeleccionado;
    }
}
