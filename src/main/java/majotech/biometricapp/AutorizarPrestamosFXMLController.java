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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Model.Cliente;
import majotech.biometricapp.Util.Util;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class AutorizarPrestamosFXMLController implements Initializable {

    @FXML
    private AnchorPane Status;
    @FXML
    private TableView<?> tableClientes;
    @FXML
    private TableColumn<?, ?> num_Cliente;
    @FXML
    private TableColumn<?, ?> sucursal;
    @FXML
    private TableColumn<?, ?> nombre;
    @FXML
    private TableColumn<?, ?> telefono;
    @FXML
    private TableColumn<?, ?> sexo;
    @FXML
    private TableColumn<?, ?> municipio;
    @FXML
    private TableColumn<?, ?> colonia;
    @FXML
    private TableColumn<?, ?> direccion;
    @FXML
    private TextField tfBuscar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void PrestarCliente(ActionEvent event) {
        Cliente clienteSeleccionado = obtenerClienteSeleccionado();
        if (clienteSeleccionado != null) {
            Util.openView("EditarClienteFXML", "Editar Cliente", EditarClienteFXMLController.class, clienteSeleccionado);
            //actualizarTablaClientes();
        } else {
            Util.showAlertWithAutoClose(Alert.AlertType.WARNING, "Advertencia", "Se debe de seleccion un cliente para editar", Duration.seconds(3));
        }
    }
    
    /*public void actualizarTablaClientes() {
        clienteList.clear();

        loadClientesFromDatabase();

        tableClientes.getItems().clear();
        tableClientes.getItems().addAll(clienteList);
    }*/
    
       /*private void loadClientesFromDatabase() {
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
    }*/
    
     private Cliente obtenerClienteSeleccionado() {
        Cliente clienteSeleccionado = (Cliente) tableClientes.getSelectionModel().getSelectedItem();
        return clienteSeleccionado;
    }
}
