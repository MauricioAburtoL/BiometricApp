module majotech.biometricapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires com.machinezoo.sourceafis;
    requires majotech;
    
    opens majotech.biometricapp to javafx.fxml;
    opens majotech.biometricapp.Model to javafx.base;
    exports majotech.biometricapp;
}
