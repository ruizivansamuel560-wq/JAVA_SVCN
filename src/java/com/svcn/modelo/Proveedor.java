package com.svcn.modelo;

import java.util.Date; 

public class Proveedor {
    
    // Nombres de atributos en Java
    private long idProveedor;
    private Date fechaIngreso;  
    private String nombreProv;  
    private String contacto;    
    private String correo;      
    private String ubicacion;   

    public Proveedor() {
    }

    // --- GETTERS Y SETTERS CORREGIDOS CON LLAVES {} (Soluciona image_283db2.png) ---
    public long getIdProveedor() { 
        return idProveedor; 
    }
    public void setIdProveedor(long idProveedor) { 
        this.idProveedor = idProveedor; 
    }

    public Date getFechaIngreso() { 
        return fechaIngreso; 
    }
    public void setFechaIngreso(Date fechaIngreso) { 
        this.fechaIngreso = fechaIngreso; 
    }

    public String getNombreProv() { 
        return nombreProv; 
    }
    public void setNombreProv(String nombreProv) { 
        this.nombreProv = nombreProv; 
    }

    public String getContacto() { 
        return contacto; 
    }
    public void setContacto(String contacto) { 
        this.contacto = contacto; 
    }

    public String getCorreo() { 
        return correo; 
    }
    public void setCorreo(String correo) { 
        this.correo = correo; 
    }

    public String getUbicacion() { 
        return ubicacion; 
    }
    public void setUbicacion(String ubicacion) { 
        this.ubicacion = ubicacion; 
    }

    // Métodos de compatibilidad
    public String getTelefono() {
        return this.contacto; 
    }
    public String getEmail() {
        return this.correo;
    }
    public void setTelefono(String telefono) { /* Opcional */ }
    public void setEmail(String email) { /* Opcional */ }
}