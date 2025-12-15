package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.UnidadMedida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UnidadMedidaDAO {

    // --- Constantes de Columna Corregidas/Añadidas ---
    private static final String ID_COL = "idUnidad"; // Usamos el nombre de la DB
    private static final String NOMBRE_COL = "nombre"; // Corregido el nombre de la columna DB
    private static final String ABREVIACION_COL = "abreviacion"; // Nueva columna DB
    private static final String TABLA = "unidadmedida";

    private UnidadMedida mapearUnidadMedida(ResultSet rs) throws SQLException {
        UnidadMedida um = new UnidadMedida();
        // CLAVE: Usamos setIdUnidad() del modelo, pero leemos el campo de la DB
        um.setIdUnidad(rs.getInt(ID_COL)); 
        um.setNombre(rs.getString(NOMBRE_COL)); // CLAVE: Usamos setNombre()
        um.setAbreviacion(rs.getString(ABREVIACION_COL)); // ¡Mapeo de la abreviación!
        return um;
    }

    // LISTAR
    public List<UnidadMedida> listarUnidadesMedida() {
        // CLAVE: Incluimos la abreviación en el SELECT
        String sql = "SELECT " + ID_COL + ", " + NOMBRE_COL + ", " + ABREVIACION_COL + " FROM " + TABLA + " ORDER BY " + NOMBRE_COL;
        List<UnidadMedida> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearUnidadMedida(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar unidades de medida: " + e.getMessage());
        }
        return lista;
    }

    // OBTENER POR ID
    public UnidadMedida obtenerUnidadMedidaPorId(int id) {
        UnidadMedida um = null;
        // CLAVE: Incluimos la abreviación en el SELECT (usando *)
        String sql = "SELECT * FROM " + TABLA + " WHERE " + ID_COL + " = ?"; 
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    um = mapearUnidadMedida(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener unidad de medida por ID: " + e.getMessage());
        }
        return um;
    }
    
    // GUARDAR (CREAR)
    public boolean guardarUnidadMedida(UnidadMedida um) {
        // CLAVE: Incluimos la abreviación en el INSERT
        String sql = "INSERT INTO " + TABLA + " (" + NOMBRE_COL + ", " + ABREVIACION_COL + ") VALUES (?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, um.getNombre()); // CLAVE: Usamos getNombre()
            ps.setString(2, um.getAbreviacion()); // ¡Abreviación!
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) { um.setIdUnidad(rs.getInt(1)); } // CLAVE: Usamos setIdUnidad()
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar la unidad de medida: " + e.getMessage());
        }
        return false;
    }
    
    // ACTUALIZAR
    public boolean actualizarUnidadMedida(UnidadMedida um) {
        // CLAVE: Incluimos la abreviación en el UPDATE
        String sql = "UPDATE " + TABLA + " SET " + NOMBRE_COL + " = ?, " + ABREVIACION_COL + " = ? WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, um.getNombre()); // CLAVE: Usamos getNombre()
            ps.setString(2, um.getAbreviacion()); // ¡Abreviación!
            ps.setInt(3, um.getIdUnidad()); // CLAVE: Usamos getIdUnidad()
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la unidad de medida: " + e.getMessage());
        }
        return false;
    }

    // ELIMINAR (No necesita cambios funcionales)
    public boolean eliminarUnidadMedida(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la unidad de medida: " + e.getMessage());
        }
        return false;
    }
}