/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package majotech.biometricapp.Model;

/**
 *
 * @author JoaquinGA
 */
public class Cliente {
    private int idCliente;
    private Integer numCliente;
    private Integer idSucursal;
    private String curp;
    private String nombre;
    private String telefono;
    private String sexo;
    private String pais;
    private String estado;
    private String municipio;
    private String colonia;
    private String direccion;
    private Boolean moroso;

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getNumCliente() {
        return numCliente;
    }

    public void setNumCliente(Integer numCliente) {
        this.numCliente = numCliente;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getMoroso() {
        return moroso;
    }

    public void setMoroso(Boolean moroso) {
        this.moroso = moroso;
    }

    @Override
    public String toString() {
        return "Cliente{" + "idCliente=" + idCliente + ", numCliente=" + numCliente + ", idSucursal=" + idSucursal + ", curp=" + curp + ", nombre=" + nombre + ", telefono=" + telefono + ", sexo=" + sexo + ", pais=" + pais + ", estado=" + estado + ", municipio=" + municipio + ", colonia=" + colonia + ", direccion=" + direccion + ", moroso=" + moroso + '}';
    }
}
