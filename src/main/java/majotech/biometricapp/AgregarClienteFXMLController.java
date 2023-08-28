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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.Util.Util;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class AgregarClienteFXMLController implements Initializable {

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
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ObservableList<String> opcionesSexo = FXCollections.observableArrayList("Hombre", "Mujer");
        CBSexo.setItems(opcionesSexo);

        // Selección por defecto (opcional)
        CBSexo.getSelectionModel().selectFirst();
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
            byte[] bmpData = obtenerDatosBMP();
             // Establecer los datos del archivo BMP en el campo BLOB en la base de datos
            statement.setBytes(13, bmpData);

            statement.executeUpdate();
        }

    }
    
    private byte[] obtenerDatosBMP() throws IOException {
        // Ruta del archivo BMP existente
        String rutaArchivoBMP = "src\\main\\java\\majotech\\biometricapp\\resources\\"+TFCurp.getText()+".bmp";

        try (FileInputStream fis = new FileInputStream(rutaArchivoBMP)) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            return buffer;
        }
    }

    @FXML
    private void Cancelar(ActionEvent event) {
    }

    //permite que solo se puedan ingresar numeros
    @FXML
    private void txtTeclaPress(KeyEvent event) {

        if (TFTelefono.getText().length() >= 10) {
            event.consume();
        }

    }

    @FXML
    private void openSensor(ActionEvent event) {

        if (TFCurp.getText().isEmpty()) {
            Util.showAlert("Campo de curp vacio", Alert.AlertType.WARNING);
            return;
        }

        if (0 != mhDevice) {

            Util.showAlert("Please close device first!", Alert.AlertType.WARNING);
            return;
        }
        int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;

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
        dedo.resize(fpWidth, fpHeight);
        mbStop = false;
        workThread = new WorkThread();
        workThread.start();
        Util.showAlert("Open succ!", Alert.AlertType.CONFIRMATION);

    }

    @FXML
    private void closeSensor(ActionEvent event) {
        FreeSensor();
        Util.showAlert("Close succ!", Alert.AlertType.CONFIRMATION);

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

            writeBitmap(imgBuf, fpWidth, fpHeight, "src\\main\\java\\majotech\\biometricapp\\resources\\" + TFCurp.getText() + ".bmp");
            dedo.setImage(new Image(new FileInputStream("src\\main\\java\\majotech\\biometricapp\\resources\\" + TFCurp.getText() + ".bmp")));

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
