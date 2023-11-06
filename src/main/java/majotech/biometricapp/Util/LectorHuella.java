/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Util;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import majotech.biometricapp.Config.Conexion;
import majotech.biometricapp.MainView;
import majotech.biometricapp.Model.Cliente;

/**
 *
 * @author JoaquinGA
 */
public class LectorHuella {

    private int id_cliente;
    private String pathImage = System.getProperty("user.dir");
    private ImageView dedo;
    private TextField TFCurp;
    private Label lbStatus;
    private List<Cliente> clienteList = new ArrayList<>();
    private TableView<Cliente> tableClientes;
    private boolean busqueda;
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

    public void abrirSensor(List<Cliente> cl, TableView<Cliente> tC, boolean b, ImageView d, TextField TF, Label lb, int id_cliente) {

        if (id_cliente != 0) {
            this.id_cliente = id_cliente;
            if (b) {
                this.busqueda = b;
                this.dedo = d;
                this.TFCurp = TF;
                this.lbStatus = lb;
            }
        } else {
            this.id_cliente = 0;
            if (b) {
                this.clienteList = cl;
                this.tableClientes = tC;
                this.busqueda = b;
                this.lbStatus = lb;

            } else {
                this.busqueda = b;
                this.dedo = d;
                this.TFCurp = TF;
                this.lbStatus = lb;
            }
        }
        if (0 != mhDevice) {
            Util.showAlertWithAutoClose(Alert.AlertType.WARNING, "Error en el Lector", "Cierre el lector o desconectelo un momento", Duration.seconds(5));
            return;
        }
        int ret;

        cbRegTemp = 0;
        bRegister = false;
        bIdentify = false;
        iFid = 1;
        enroll_idx = 0;
        if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
            Util.showAlert("Fallo al inicar el Lector!", Alert.AlertType.ERROR);
            return;
        }
        ret = FingerprintSensorEx.GetDeviceCount();
        if (ret < 0) {
            Util.showAlert("Ningun Dispositivo Conectado!", Alert.AlertType.WARNING);
            FreeSensor();
            return;
        }
        if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
            Util.showAlert("Fallo con el lector, ret = " + ret + "!", Alert.AlertType.ERROR);
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
        lbStatus.setText("Activo");
        lbStatus.setTextFill(Color.RED);
        WorkThread workThread = new WorkThread();
        workThread.start();
        Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Lector Listo", "Sensor listo", Duration.seconds(3));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(10000); // Espera durante 2 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                closeSensor();
                // Cambia el color del Label a azul en el hilo de JavaFX
                Platform.runLater(() -> lbStatus.setTextFill(Color.GREEN));
                Platform.runLater(() -> lbStatus.setText("Desactivado"));
                return null;

            }
        };

        new Thread(task).start();
    }

    public void closeSensor() {
        FreeSensor();
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
                        if (0 == ret && (byte) (nFakeStatus & 31) != 31) {
                            Util.showAlert("Esto es un dedo?", Alert.AlertType.WARNING);
                            return;
                        }
                    }
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
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
            if (busqueda) {
                if (id_cliente != 0) {
                    writeBitmap(imgBuf, fpWidth, fpHeight, pathImage + "fingerprintBusqueda.bmp");
                    FreeSensor();
                    if (!prueba(0)) {
                        Util.showAlert("Error al momento de hacer la comparacion: Verifica el lector", Alert.AlertType.ERROR);
                    }
                } else {
                    writeBitmap(imgBuf, fpWidth, fpHeight, pathImage + "fingerprintBusqueda.bmp");
                    dedo.setImage(new Image(new FileInputStream(pathImage + "fingerprintBusqueda.bmp")));
                    if (prueba(id_cliente)) {
                        System.out.println("Existe");
                    }
                }

            } else {
                writeBitmap(imgBuf, fpWidth, fpHeight, pathImage + TFCurp.getText() + ".bmp");
                dedo.setImage(new Image(new FileInputStream(pathImage + TFCurp.getText() + ".bmp")));
                FreeSensor();
            }

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

        lbStatus.setTextFill(Color.GREEN);
        Platform.runLater(() -> {
            lbStatus.setText("Desactivado");
        });
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

    public boolean prueba(int id_cliente) {
        Map<Integer, byte[]> fingerprintsMap = new HashMap<>();
        String pathGen = System.getProperty("user.dir");
        if (id_cliente != 0) {

            Conexion connection = new Conexion();
            String sql = "SELECT Huella FROM cliente WHERE id_cliente = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                if (preparedStatement == null) {
                    Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                    return false;
                }
                preparedStatement.setInt(1, id_cliente);
                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        byte[] fingerprintBytes = result.getBytes("Huella");
                        fingerprintsMap.put(id_cliente, fingerprintBytes);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            fingerprintsMap = fetchFingerprintsFromDatabase();
        }
        if (fingerprintsMap == null) {
            return false;
        }
        byte[] probeBytes;
        try {
            probeBytes = Files.readAllBytes(Paths.get(pathGen + "fingerprintBusqueda.bmp"));

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
                    if(id_cliente != 0){
                        return true;
                    }
                    // Seleccionar el registro en la tabla correspondiente al ID
                    for (Cliente cliente : clienteList) {
                        if (cliente.getIdCliente() == id) {
                            Platform.runLater(() -> {
                                Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Usuario encontrado", "El usuario encontrado es: " + cliente.getNombre(), Duration.seconds(3));
                            });

                            reloadTable(cliente);
                            tableClientes.getSelectionModel().select(cliente);

                            return true;
                        }
                    }

                }

            }
            Platform.runLater(() -> {
                Util.showAlertWithAutoClose(Alert.AlertType.INFORMATION, "Usuario no encontrado", "No se encontro ningun usuario con esta huella", Duration.ZERO);
            });
        } catch (IOException ex) {
            Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error prueba", ex.toString(), Duration.seconds(10));
            Logger.getLogger(LectorHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static Map<Integer, byte[]> fetchFingerprintsFromDatabase() {
        Map<Integer, byte[]> fingerprintsMap = new HashMap<>();
        Conexion connection = new Conexion();
        String query = "SELECT id_cliente, Huella FROM clientes";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (statement == null) {
                Util.showAlertWithAutoClose(Alert.AlertType.ERROR, "Error en la BD", "Hay un error al conectar a la bd, no se realizara ninguna accion", Duration.seconds(3));
                return null;
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_cliente");
                    if (resultSet.getBytes("Huella") != null) {
                        byte[] fingerprintBytes = resultSet.getBytes("Huella");
                        fingerprintsMap.put(id, fingerprintBytes);
                    }

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fingerprintsMap;
    }

    private void reloadTable(Cliente c) {
        List<Cliente> clienteList2 = new ArrayList<>();
        if (c != null) {
            clienteList2.add(c);
        }
        for (Cliente cliente : clienteList) {
            if (!c.equals(cliente)) {
                clienteList2.add(cliente);
            }
        }
        clienteList.clear();
        clienteList = clienteList2;
        tableClientes.getItems().clear();
        tableClientes.getItems().addAll(clienteList);
    }
}
