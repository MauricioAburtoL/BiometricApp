/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Model;

/**
 *
 * @author JoaquinGA
 */
public class Usuario {
    private int idUsuario;
    private String Nombre;
    private int Tipo;
    private int idSucursal;
    private String email;
    private String pass;
    private int activo;

    public Usuario() {
    }

    public Usuario(int idUsuario, String Nombre, int Tipo, int idSucursal, String email, String pass, int activo) {
        this.idUsuario = idUsuario;
        this.Nombre = Nombre;
        this.Tipo = Tipo;
        this.idSucursal = idSucursal;
        this.email = email;
        this.pass = pass;
        this.activo = activo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public int getTipo() {
        return Tipo;
    }

    public void setTipo(int Tipo) {
        this.Tipo = Tipo;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }
    
    
    
    
}
