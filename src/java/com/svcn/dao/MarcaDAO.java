package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Marca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    // Nombres de columna de la DB para MARCAS
    private static final String ID_COL = "idMarca";
    private static final String NOMBRE_COL = "nombreMarca";
    private static final String TABLA = "marcas";

    // Mapeo de ResultSet a objeto Marca
    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca m = new Marca();
        m.setIdMarca(rs.getInt(ID_COL));
        m.setNombreMarca(rs.getString(NOMBRE_COL));
        return m;
    }

    // --- MÉTODOS CRUD ---

    public List<Marca> listarMarcas() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT " + ID_COL + ", " + NOMBRE_COL + " FROM " + TABLA + " ORDER BY " + NOMBRE_COL;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar marcas: " + e.getMessage());
        }
        return lista;
    }
    
    public Marca obtenerMarcaPorId(int id) {
        Marca m = null;
        String sql = "SELECT " + ID_COL + ", " + NOMBRE_COL + " FROM " + TABLA + " WHERE " + ID_COL + " = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m = mapearMarca(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener marca por ID: " + e.getMessage());
        }
        return m;
    }

    public boolean guardarMarca(Marca m) {
        String sql = "INSERT INTO " + TABLA + " (" + NOMBRE_COL + ") VALUES (?)";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, m.getNombreMarca());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setIdMarca(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar la marca: " + e.getMessage());
        }
        return exito;
    }

    public boolean actualizarMarca(Marca m) {
        String sql = "UPDATE " + TABLA + " SET " + NOMBRE_COL + " = ? WHERE " + ID_COL + " = ?";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, m.getNombreMarca());
            ps.setInt(2, m.getIdMarca());

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la marca: " + e.getMessage());
        }
        return exito;
    }

    public boolean eliminarMarca(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE " + ID_COL + " = ?";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la marca: " + e.getMessage());
        }
        return exito;
    }
}