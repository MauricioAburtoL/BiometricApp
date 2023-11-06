/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Model;

/**
 *
 * @author JoaquinGA
 */
public class Sucursal {
    private int id_sucursal;
    private String nombre;
    private String municipiio;
    private String colonia;
    
    public Sucursal(){}
    public Sucursal(int id_sucursal, String nombre, String municipiio, String colonia) {
        this.id_sucursal = id_sucursal;
        this.nombre = nombre;
        this.municipiio = municipiio;
        this.colonia = colonia;
    }

    
    public int getId_sucursal() {
        return id_sucursal;
    }

    public void setId_sucursal(int id_sucursal) {
        this.id_sucursal = id_sucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMunicipiio() {
        return municipiio;
    }

    public void setMunicipiio(String municipiio) {
        this.municipiio = municipiio;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }
    
    
    
    
}
