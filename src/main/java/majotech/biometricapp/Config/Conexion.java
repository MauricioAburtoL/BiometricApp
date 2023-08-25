/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Config;

import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import majotech.biometricapp.Util.Util;


/**
 *
 * @author JoaquinGA
 */
public class Conexion {
    private String ip = "localhost";
    private String port = "3306";
    private String bdName = "Prueba";
    private String jdbcUrl = "jdbc:mysql://" + ip + ":" + port + "/" + bdName;
    private String user = "root";
    private String password = "";

    public Connection EstablecerConexion() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
            if (connection != null) {
                return connection;
            }else{
                return null;
            }
        } catch (ClassNotFoundException e) {
            Util.showAlert("Controlador JDBC no encontrado: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            Util.showAlert("Error de conexión a la base de datos: " + e.getMessage(),Alert.AlertType.ERROR);
        }
        return null;
    }
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        Connection connection = EstablecerConexion();
        if (connection != null) {
            return connection.prepareStatement(sql);
        } else {
            return null;
        }
    }
}
