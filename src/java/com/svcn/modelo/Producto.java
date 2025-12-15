package com.svcn.modelo;

// Importar EstiloPrenda si lo estás usando como Enum
import com.svcn.modelo.EstiloPrenda; 

public class Producto {

    // --- 1. ATRIBUTOS PRIMARIOS Y TIPOS DE DATOS CORREGIDOS (long) ---
    private long idProducto; 
    private String nombre;
    private String descripcion;
    private double precioUni;
    // Stock no está implementado aún (se incluye en el DAO, pero no en este modelo actual)
    
    // Atributos adicionales que maneja el DAO
    private String material;
    private String categoria;
    private String tipoPrenda;
    private EstiloPrenda estilo; // Asumiendo que EstiloPrenda es un Enum
    private String despCuidados;
    private String disponibilidad;
    private String rutaImagen;
    private long idAdmin;

    // --- 2. CLAVES FORÁNEAS (FKs) CORREGIDAS A long ---
    private long idProveedor;
    private long idMarca;
    private long idTalla;
    private long idColor;
    private long idMedida; // FK para UnidadMedida (idMedida)

    // --- 3. CONSTRUCTORES ---
    
    public Producto() {
        // Constructor vacío requerido por muchos frameworks y DAOs
    }
    
    // Opcionalmente, puedes añadir un constructor con todos los campos si lo necesitas
    
    // --- 4. GETTERS Y SETTERS COMPLETOS ---
    
    // --- Getters y Setters de IDs (long) ---
    
    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }
    
    public long getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(long idAdmin) {
        this.idAdmin = idAdmin;
    }

    public long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public long getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(long idMarca) {
        this.idMarca = idMarca;
    }

    public long getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(long idTalla) {
        this.idTalla = idTalla;
    }

    public long getIdColor() {
        return idColor;
    }

    public void setIdColor(long idColor) {
        this.idColor = idColor;
    }

    // CLAVE: Unidad de Medida (usando 'idMedida' para el nombre del campo)
    public long getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(long idMedida) {
        this.idMedida = idMedida;
    }

    // --- Getters y Setters de Atributos Básicos ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioUni() {
        return precioUni;
    }

    public void setPrecioUni(double precioUni) {
        this.precioUni = precioUni;
    }
    
    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
    
    // --- Getters y Setters de Atributos Adicionales del DAO ---
    
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(String tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public EstiloPrenda getEstilo() {
        return estilo;
    }

    public void setEstilo(EstiloPrenda estilo) {
        this.estilo = estilo;
    }
    
    // Setter de Estilo que acepta String, útil para el controlador/DAO
    public void setEstilo(String estiloStr) {
        if (estiloStr != null && !estiloStr.trim().isEmpty()) {
            try {
                this.estilo = EstiloPrenda.valueOf(estiloStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.estilo = null; // O manejar el error según la lógica de tu negocio
            }
        } else {
            this.estilo = null;
        }
    }

    public String getDespCuidados() {
        return despCuidados;
    }

    public void setDespCuidados(String despCuidados) {
        this.despCuidados = despCuidados;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    
    // --- Gestión de Stock (Comentado Temporalmente) ---
    /*
    private int cantidadStock; 
    
    public int getCantidadStock() {
        return cantidadStock;
    }
    
    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }
    */
}