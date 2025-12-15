package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Administrador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO {

    public boolean agregarAdministrador(Administrador admin) {
        // La sentencia SQL usa 'contrasena'
        String sql = "INSERT INTO administrador (nombre, correo, telefono, contrasena, rol) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, admin.getNombre());
            ps.setString(2, admin.getCorreo());
            ps.setString(3, admin.getTelefono()); 
            ps.setString(4, admin.getContrasena()); 
            
            ps.setString(5, admin.getRol());    
            
            int filasAfectadas = ps.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar administrador (DAO): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Código de AdministradorDAO.java

    public Administrador validarLogin(String correo, String contrasena) {
        Administrador admin = null;
        String sql = "SELECT * FROM administrador WHERE correo = ? AND contrasena = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Administrador();
                    
                    // -- ¡VERIFICACIÓN DE MAPEO (DEBE FUNCIONAR) --
                    admin.setIdAdmin(rs.getLong("idAdmin")); 
                    admin.setNombre(rs.getString("nombre")); 
                    admin.setCorreo(rs.getString("correo")); 
                    admin.setTelefono(rs.getString("telefono")); 
                    admin.setRol(rs.getString("rol")); 
                    
                    // Se asume que en el modelo se llama setContrasena
                    admin.setContrasena(rs.getString("contrasena")); 
                    // ---------------------------------------------
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar administrador: " + e.getMessage());
        }
        return admin;
    }
}