package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO {

    private static final String ID_COL = "idColor";
    private static final String NOMBRE_COL = "nombreColor";
    private static final String TABLA = "color";

    private Color mapearColor(ResultSet rs) throws SQLException {
        Color c = new Color();
        c.setIdColor(rs.getInt(ID_COL));
        c.setNombreColor(rs.getString(NOMBRE_COL));
        return c;
    }

    // LISTAR
    public List<Color> listarColores() {
        List<Color> lista = new ArrayList<>();
        String sql = "SELECT " + ID_COL + ", " + NOMBRE_COL + " FROM " + TABLA + " ORDER BY " + NOMBRE_COL;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearColor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar colores: " + e.getMessage());
        }
        return lista;
    }

    // OBTENER POR ID
    public Color obtenerColorPorId(int id) {
        Color c = null;
        String sql = "SELECT * FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = mapearColor(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener color por ID: " + e.getMessage());
        }
        return c;
    }
    
    // GUARDAR (CREAR)
    public boolean guardarColor(Color c) {
        String sql = "INSERT INTO " + TABLA + " (" + NOMBRE_COL + ") VALUES (?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombreColor());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) { c.setIdColor(rs.getInt(1)); }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el color: " + e.getMessage());
        }
        return false;
    }
    
    // ACTUALIZAR
    public boolean actualizarColor(Color c) {
        String sql = "UPDATE " + TABLA + " SET " + NOMBRE_COL + " = ? WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombreColor());
            ps.setInt(2, c.getIdColor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el color: " + e.getMessage());
        }
        return false;
    }

    // ELIMINAR
    public boolean eliminarColor(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el color: " + e.getMessage());
        }
        return false;
    }
}