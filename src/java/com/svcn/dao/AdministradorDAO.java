package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Administrador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO {

    public Administrador validarLogin(String correo, String contrasena) {
        Administrador admin = null;
        // 🚨 CRÍTICO: Usa 'correo' y 'contrasena' de tu tabla administrador 🚨
        String sql = "SELECT * FROM administrador WHERE correo = ? AND contrasena = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Administrador();
                    admin.setIdAdmin(rs.getLong("id_admin"));
                    admin.setNombre(rs.getString("nombre"));
                    admin.setCorreo(rs.getString("correo"));
                    admin.setRol(rs.getString("rol")); 
                    admin.setContrasena(rs.getString("contrasena")); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar administrador: " + e.getMessage());
        }
        return admin;
    }
}