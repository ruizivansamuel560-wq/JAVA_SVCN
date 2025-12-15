package com.svcn.modelo;

public class UnidadMedida {
    
    // Atributos corregidos para coincidir con el JSP:
    private int idUnidad; // Antes idMedida
    private String nombre; // Antes nombreMedida
    private String abreviacion; // ¡Nuevo campo requerido por el JSP!

    public UnidadMedida() {
        // Constructor vacío necesario
    }

    // Constructor con campos (Opcional pero útil)
    public UnidadMedida(int idUnidad, String nombre, String abreviacion) {
        this.idUnidad = idUnidad;
        this.nombre = nombre;
        this.abreviacion = abreviacion;
    }
    
    // --- Getters y Setters corregidos ---
    
    // CLAVE: Corregido a getIdUnidad()
    public int getIdUnidad() {
        return idUnidad;
    }

    // CLAVE: Corregido a setIdUnidad()
    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    // CLAVE: Corregido a getNombre()
    public String getNombre() {
        return nombre;
    }

    // CLAVE: Corregido a setNombre()
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // CLAVE: Nuevo método para la abreviación
    public String getAbreviacion() {
        return abreviacion;
    }

    // CLAVE: Nuevo método para la abreviación
    public void setAbreviacion(String abreviacion) {
        this.abreviacion = abreviacion;
    }
}