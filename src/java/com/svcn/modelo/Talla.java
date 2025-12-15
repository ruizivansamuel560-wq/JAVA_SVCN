package com.svcn.modelo;

public class Talla {
    private int idTalla;
    private String nombreTalla;

    public Talla() {
    }

    // Getters y Setters
    public int getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(int idTalla) {
        this.idTalla = idTalla;
    }

    public String getNombreTalla() {
        return nombreTalla;
    }

    public void setNombreTalla(String nombreTalla) {
        this.nombreTalla = nombreTalla;
    }
}