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
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import static majotech.biometricapp.Busqueda.writeBitmap;

/**
 * FXML Controller class
 *
 * @author JoaquinGA
 */
public class MainView implements Initializable {
    private long mhDevice = 0;
    private int cbRegTemp = 0;
    private int iFid = 1;
    private int enroll_idx = 0;
    private long mhDB = 0;
    int fpWidth = 0;
    int fpHeight = 0;
    private byte[] imgbuf = null;
    private boolean mbStop = true;
    private WorkThread workThread = null;
    private int[] templateLen = new int[1];
    private int nFakeFunOn = 1;
    private byte[] template = new byte[2048];
    private boolean bRegister = false;
    private byte[][] regtemparray = new byte[3][2048];
    private byte[] lastRegTemp = new byte[2048];
    private boolean bIdentify = true;
    
    

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
    
    private Cliente obtenerClienteSeleccionado() {
    return tableClientes.getSelectionModel().getSelectedItem();
}
    
    private int obtenerNumClienteSeleccionado() {
    Cliente clienteSeleccionado = obtenerClienteSeleccionado();
    if (clienteSeleccionado != null) {
        return clienteSeleccionado.getNumCliente();
    }
    return -1; // O algún valor que indique que no se seleccionó ningún cliente
}
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
        if (0 != mhDevice) {
            
            Util.showAlert("Please close device first!", Alert.AlertType.WARNING);
            return;
        }
        int ret ;
      
        cbRegTemp = 0;
        bRegister = false;
        bIdentify = false;
        iFid = 1;
        enroll_idx = 0;
        if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
            Util.showAlert("Init failed!", Alert.AlertType.ERROR);
            return;
        }
        ret = FingerprintSensorEx.GetDeviceCount();
        if (ret < 0) {
            Util.showAlert("No devices connected!", Alert.AlertType.WARNING);
            FreeSensor();
            return;
        }
        if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
            Util.showAlert("Open device fail, ret = " + ret + "!", Alert.AlertType.ERROR);
            FreeSensor();
            return;
        }
        if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
            Util.showAlert("Init DB fail, ret = " + ret + "!", Alert.AlertType.WARNING);
            FreeSensor();
            return;
        }

        byte[] paramValue = new byte[4];
        int[] size = new int[1];

        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
        fpWidth = byteArrayToInt(paramValue);
        size[0] = 4;
        FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
        fpHeight = byteArrayToInt(paramValue);
        
        imgbuf = new byte[fpWidth * fpHeight];
        mbStop = false;
        workThread = new WorkThread();
        workThread.start();
        Util.showAlert("Open succ!", Alert.AlertType.CONFIRMATION);
        
    }

    @FXML
    private void AgregarCliente(ActionEvent event) {
    }

    @FXML
    private void EditarCliente(ActionEvent event) {
    }

    @FXML
    private void EliminarCliente(ActionEvent event) throws SQLException, IOException {
    Conexion connection = new Conexion();
    int numClienteSeleccionado = obtenerNumClienteSeleccionado();

    if (numClienteSeleccionado != -1) {
        String sql = "DELETE FROM clientes WHERE num_cliente = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, numClienteSeleccionado);
            statement.executeUpdate();
        }
    } else {
        // Mostrar un mensaje o realizar alguna acción en caso de que no se haya seleccionado un cliente
    }
}

    public void prueba() {
        String pathGen = "src\\main\\java\\majotech\\biometricapp\\resources\\";
        try {
            Map<Integer, byte[]> fingerprintsMap = fetchFingerprintsFromDatabase();
            byte[] probeBytes = Files.readAllBytes(Paths.get(pathGen + "fingerprintBusqueda.bmp"));
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
            Logger.getLogger(MainView1.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MainView1.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fingerprintsMap;
    }
    
    
    private void FreeSensor() {
        mbStop = true;
        try { 
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        }
        if (0 != mhDB) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
    }
    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
       
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }
    private class WorkThread extends Thread {

        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))) {
                    if (nFakeFunOn == 1) {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = " + ret + ",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte) (nFakeStatus & 31) != 31) {
                            Util.showAlert("Is a fake-finer?", Alert.AlertType.WARNING);
                            return;
                        }
                    }
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
                    System.out.println("strBase64=" + strBase64);
                    
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        private void runOnUiThread(Runnable runnable) {
            

        }
    }
    
    private void OnCatpureOK(byte[] imgBuf) {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "src\\main\\java\\majotech\\biometricapp\\resources\\fingerprintBusqueda.bmp");
            FreeSensor();
            prueba();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }

    private void OnExtractOK(byte[] template, int len) {
        if (bRegister) {
            int[] fid = new int[1];
            int[] score = new int[1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
            if (ret == 0) {
                Util.showAlert("the finger already enroll by " + fid[0] + ",cancel enroll", Alert.AlertType.NONE);
                bRegister = false;
                enroll_idx = 0;
                return;
            }
            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx - 1], template) <= 0) {
                Util.showAlert("please press the same finger 3 times for the enrollment", Alert.AlertType.NONE);
                return;
            }
            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
            enroll_idx++;
            if (enroll_idx == 3) {
                int[] _retLen = new int[1];
                _retLen[0] = 2048;
                byte[] regTemp = new byte[_retLen[0]];

                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2],
                        regTemp, _retLen))
                        && 0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                    iFid++;
                    cbRegTemp = _retLen[0];
                    System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(regTemp, cbRegTemp);
                    
                    Util.showAlert("enroll succ", Alert.AlertType.NONE);
                } else {
                    Util.showAlert("enroll fail, error code=" + ret, Alert.AlertType.NONE);
                }
                bRegister = false;
            } else {
                Util.showAlert("You need to press the " + (3 - enroll_idx) + " times fingerprint", Alert.AlertType.NONE);
            }
        } else {
            if (bIdentify) {
                int[] fid = new int[1];
                int[] score = new int[1];
                int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                if (ret == 0) {
                    Util.showAlert("Identify succ, fid=" + fid[0] + ",score=" + score[0], Alert.AlertType.NONE);
                } else {
                    Util.showAlert("Identify fail, errcode=" + ret, Alert.AlertType.NONE);
                }

            } else {
                if (cbRegTemp <= 0) {
                    Util.showAlert("Please register first!", Alert.AlertType.NONE);
                } else {
                    int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
                    if (ret > 0) {
                        Util.showAlert("Verify succ, score=" + ret, Alert.AlertType.NONE);
                    } else {
                        Util.showAlert("Verify fail, ret=" + ret, Alert.AlertType.NONE);
                    }
                }
            }
        }
    }

    public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
            String path) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int w = (((nWidth + 3) / 4) * 4);
        int bfType = 0x424d; 
        int bfSize = 54 + 1024 + w * nHeight;
        int bfReserved1 = 0;
        int bfReserved2 = 0;
        int bfOffBits = 54 + 1024;

        dos.writeShort(bfType); 
        dos.write(changeByte(bfSize), 0, 4);
        dos.write(changeByte(bfReserved1), 0, 2);
        dos.write(changeByte(bfReserved2), 0, 2);
        dos.write(changeByte(bfOffBits), 0, 4);

        int biSize = 40;
        int biWidth = nWidth;
        int biHeight = nHeight;
        int biPlanes = 1; 
        int biBitcount = 8;
        int biCompression = 0;
        int biSizeImage = w * nHeight;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 0;
        int biClrImportant = 0;

        dos.write(changeByte(biSize), 0, 4);
        dos.write(changeByte(biWidth), 0, 4);
        dos.write(changeByte(biHeight), 0, 4);
        dos.write(changeByte(biPlanes), 0, 2);
        dos.write(changeByte(biBitcount), 0, 2);
        dos.write(changeByte(biCompression), 0, 4);
        dos.write(changeByte(biSizeImage), 0, 4);
        dos.write(changeByte(biXPelsPerMeter), 0, 4);
        dos.write(changeByte(biYPelsPerMeter), 0, 4);
        dos.write(changeByte(biClrUsed), 0, 4);
        dos.write(changeByte(biClrImportant), 0, 4);

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth) {
            filter = new byte[w - nWidth];
        }

        for (int i = 0; i < nHeight; i++) {
            dos.write(imageBuf, (nHeight - 1 - i) * nWidth, nWidth);
            if (w > nWidth) {
                dos.write(filter, 0, w - nWidth);
            }
        }
        dos.flush();
        dos.close();
        fos.close();
    }

    public static byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    public static byte[] intToByteArray(final int number) {
        byte[] abyte = new byte[4];
       
        abyte[0] = (byte) (0xff & number);
      
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }
}