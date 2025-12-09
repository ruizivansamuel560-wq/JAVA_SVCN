package com.svcn.dao;

import com.svcn.config.Conexion; // <<-- DEBE SER 'com.svcn.config' o el que uses
import com.svcn.modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        // 🚨 VERIFICAR: id_productos, precio_uni, ruta_imagen, disponibilidad 🚨
        String sql = "SELECT id_productos, nombre, precio_uni, descripcion, ruta_imagen FROM productos WHERE disponibilidad = TRUE LIMIT 3"; 

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                // 🚨 VERIFICAR: nombres de las columnas en rs.get...() 🚨
                p.setIdProducto(rs.getLong("id_productos")); 
                p.setNombre(rs.getString("nombre"));
                p.setPrecioUni(rs.getDouble("precio_uni"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setRutaImagen(rs.getString("ruta_imagen"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("--- ERROR CRÍTICO EN PRODUCTO DAO (SQL) ---");
            System.err.println("Mensaje de SQL (busca aquí el error de columna): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}