package com.svcn.modelo;

public class Administrador {
    
    private long idAdmin;
    private String nombre;
    private String correo; 
    private String telefono;
    private String contrasena; 
    private String rol;

    public Administrador() { }
    
    // Getters y Setters
    public long getIdAdmin() { return idAdmin; }
    public void setIdAdmin(long idAdmin) { this.idAdmin = idAdmin; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}