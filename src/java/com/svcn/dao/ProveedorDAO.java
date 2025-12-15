package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    
    // Nombres de columna de la DB (Basados en tu tabla)
    private static final String ID_COL = "idProveedor";
    private static final String NOMBRE_COL = "nombreProv";
    private static final String CONTACTO_COL = "contacto";
    private static final String CORREO_COL = "correo";
    private static final String UBICACION_COL = "ubicacion";
    private static final String FECHA_COL = "fechaIngreso";

    // Método auxiliar para mapear un ResultSet a un objeto Proveedor
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        
        // Asignación de valores
        p.setIdProveedor(rs.getLong(ID_COL)); 
        p.setNombreProv(rs.getString(NOMBRE_COL));
        
        p.setContacto(rs.getString(CONTACTO_COL)); 
        p.setCorreo(rs.getString(CORREO_COL));   
        p.setUbicacion(rs.getString(UBICACION_COL));
        
        p.setFechaIngreso(rs.getDate(FECHA_COL));
        
        return p;
    }

    // --- MÉTODOS CRUD ---

    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT " + ID_COL + ", " + FECHA_COL + ", " + NOMBRE_COL + ", " + CONTACTO_COL + ", " + CORREO_COL + ", " + UBICACION_COL + " FROM proveedores ORDER BY " + NOMBRE_COL;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    
    public Proveedor obtenerProveedorPorId(long idProveedor) {
        Proveedor p = null;
        String sql = "SELECT " + ID_COL + ", " + FECHA_COL + ", " + NOMBRE_COL + ", " + CONTACTO_COL + ", " + CORREO_COL + ", " + UBICACION_COL + " FROM proveedores WHERE " + ID_COL + " = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idProveedor);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapearProveedor(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return p;
    }

    public boolean guardarProveedor(Proveedor p) {
        // Se excluye FECHA_COL y se permite que la DB asigne el valor por defecto.
        String sql = "INSERT INTO proveedores (" + NOMBRE_COL + ", " + CONTACTO_COL + ", " + CORREO_COL + ", " + UBICACION_COL + ") VALUES (?, ?, ?, ?)";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, p.getNombreProv());
            ps.setString(2, p.getContacto());
            ps.setString(3, p.getCorreo());
            ps.setString(4, p.getUbicacion()); 

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                exito = true;
                // Asigna el ID generado al objeto
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdProveedor(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return exito;
    }

    public boolean actualizarProveedor(Proveedor p) {
        String sql = "UPDATE proveedores SET " + NOMBRE_COL + " = ?, " + CONTACTO_COL + " = ?, " + CORREO_COL + " = ?, " + UBICACION_COL + " = ? WHERE " + ID_COL + " = ?";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getNombreProv());
            ps.setString(2, p.getContacto());
            ps.setString(3, p.getCorreo());
            ps.setString(4, p.getUbicacion());
            ps.setLong(5, p.getIdProveedor());

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return exito;
    }

    public boolean eliminarProveedor(long idProveedor) {
        String sql = "DELETE FROM proveedores WHERE " + ID_COL + " = ?";
        boolean exito = false;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idProveedor);

            if (ps.executeUpdate() > 0) {
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return exito;
    }
}