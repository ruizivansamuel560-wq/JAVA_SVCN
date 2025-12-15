package com.svcn.modelo;

public class Color {
    private int idColor;
    private String nombreColor;

    public Color() {
    }

    // Getters y Setters
    public int getIdColor() {
        return idColor;
    }

    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }
}