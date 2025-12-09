package com.svcn.modelo;

// POJO (Plain Old Java Object)
public class Producto {
    
    private long idProducto;
    private String nombre;
    private double precioUni; 
    private String descripcion;
    private String rutaImagen; 

    public Producto() {}
    // ... (Asegúrate de que tus Getters y Setters estén completos) ...

    public long getIdProducto() { return idProducto; }
    public void setIdProducto(long idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecioUni() { return precioUni; }
    public void setPrecioUni(double precioUni) { this.precioUni = precioUni; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
}