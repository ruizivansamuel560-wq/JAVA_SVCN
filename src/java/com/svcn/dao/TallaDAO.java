package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Talla;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TallaDAO {

    private static final String ID_COL = "idTalla";
    private static final String NOMBRE_COL = "nombreTalla";
    private static final String TABLA = "talla";

    private Talla mapearTalla(ResultSet rs) throws SQLException {
        Talla t = new Talla();
        t.setIdTalla(rs.getInt(ID_COL));
        t.setNombreTalla(rs.getString(NOMBRE_COL));
        return t;
    }

    // LISTAR
    public List<Talla> listarTallas() {
        List<Talla> lista = new ArrayList<>();
        String sql = "SELECT " + ID_COL + ", " + NOMBRE_COL + " FROM " + TABLA + " ORDER BY " + NOMBRE_COL;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearTalla(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tallas: " + e.getMessage());
        }
        return lista;
    }
    
    // OBTENER POR ID
    public Talla obtenerTallaPorId(int id) {
        Talla t = null;
        String sql = "SELECT * FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = mapearTalla(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener talla por ID: " + e.getMessage());
        }
        return t;
    }

    // GUARDAR (CREAR)
    public boolean guardarTalla(Talla t) {
        String sql = "INSERT INTO " + TABLA + " (" + NOMBRE_COL + ") VALUES (?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombreTalla());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) { t.setIdTalla(rs.getInt(1)); }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar la talla: " + e.getMessage());
        }
        return false;
    }
    
    // ACTUALIZAR
    public boolean actualizarTalla(Talla t) {
        String sql = "UPDATE " + TABLA + " SET " + NOMBRE_COL + " = ? WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNombreTalla());
            ps.setInt(2, t.getIdTalla());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la talla: " + e.getMessage());
        }
        return false;
    }

    // ELIMINAR
    public boolean eliminarTalla(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la talla: " + e.getMessage());
        }
        return false;
    }
}