package com.svcn.dao;

import com.svcn.config.Conexion;
import com.svcn.modelo.EstiloPrenda;
import com.svcn.modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ProductoDAO {
    
    // Lista de columnas usando CamelCase (asumiendo que así se llaman en la DB)
    private final String COLUMNAS_PRODUCTO =
    "idProducto, nombre, precioUni, descripcion, rutaImagen, idAdmin, " +
    "material, categoria, tipoPrenda, estilo, despCuidados, disponibilidad, " +
    "idProveedor, idMarca, idTalla, idColor, idMedida"; 
    
    /**
     * Método auxiliar para mapear todas las columnas del ResultSet al objeto Producto.
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        
        // Usamos CamelCase para obtener los valores del ResultSet
        p.setIdProducto(rs.getLong("idProducto"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecioUni(rs.getDouble("precioUni"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setRutaImagen(rs.getString("rutaImagen"));
        p.setIdAdmin(rs.getLong("idAdmin"));
        p.setDisponibilidad(rs.getString("disponibilidad"));
        p.setIdProveedor(rs.getLong("idProveedor"));
        
        // Mapeo de FKs de Catálogo (Todos como long)
        p.setIdMarca(rs.getLong("idMarca"));
        p.setIdTalla(rs.getLong("idTalla"));
        p.setIdColor(rs.getLong("idColor"));
        p.setIdMedida(rs.getLong("idMedida")); 
        
        // Mapeo de otros campos (CamelCase)
        p.setMaterial(rs.getString("material"));
        p.setCategoria(rs.getString("categoria"));
        p.setTipoPrenda(rs.getString("tipoPrenda")); 
        
        String estiloStr = rs.getString("estilo");
        if (estiloStr != null) {
            try {
                // El método setEstilo en el modelo maneja la conversión de String a Enum
                p.setEstilo(estiloStr); 
            } catch (Exception e) {
                // Manejo de error si el valor del enum no es válido
                p.setEstilo((EstiloPrenda) null);
            }
        }
        
        p.setDespCuidados(rs.getString("despCuidados"));
        
        return p;
    }
    
    // C - CREATE: AGREGAR UN NUEVO PRODUCTO
    public boolean agregarProducto(Producto p) {
        String sql = "INSERT INTO productos (" +
                      "nombre, precioUni, descripcion, rutaImagen, idAdmin, material, categoria, tipoPrenda, estilo, despCuidados, disponibilidad, " +
                      "idProveedor, idMarca, idTalla, idColor, idMedida" + 
                      ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        
         try (Connection conn = Conexion.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                int idx = 1;
                ps.setString(idx++, p.getNombre());
                ps.setDouble(idx++, p.getPrecioUni());
                ps.setString(idx++, p.getDescripcion());
                ps.setString(idx++, p.getRutaImagen());
                ps.setLong(idx++, p.getIdAdmin() > 0 ? p.getIdAdmin() : 1); 
                
                // Campos de atributos (7 campos)
                ps.setString(idx++, p.getMaterial());
                ps.setString(idx++, p.getCategoria());
                ps.setString(idx++, p.getTipoPrenda());
                ps.setString(idx++, p.getEstilo() != null ? p.getEstilo().name() : null); 
                ps.setString(idx++, p.getDespCuidados());
                ps.setString(idx++, p.getDisponibilidad());
                
                // Claves Foráneas (5 campos)
                ps.setLong(idx++, p.getIdProveedor());
                ps.setLong(idx++, p.getIdMarca());
                ps.setLong(idx++, p.getIdTalla());
                ps.setLong(idx++, p.getIdColor());
                ps.setLong(idx++, p.getIdMedida()); 
                
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setIdProducto(rs.getLong(1));
                        }
                    }
                    return true;
                }
                return false;
                
            } catch (SQLException e) {
                System.err.println("Error al agregar producto (DAO): " + e.getMessage());
                e.printStackTrace();
                return false;
            }
    }
    
    // R - READ ALL: LISTAR TODOS LOS PRODUCTOS
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS_PRODUCTO + " FROM productos";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace(); 
        }
        return lista;
    }

    // R - READ ONE: OBTENER UN PRODUCTO POR SU ID
    public Producto obtenerProductoPorId(long id) {
        Producto p = null;
        String sql = "SELECT " + COLUMNAS_PRODUCTO + " FROM productos WHERE idProducto = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return p;
    }

    // U - UPDATE: ACTUALIZAR UN PRODUCTO EXISTENTE
    public boolean actualizarProducto(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, precioUni = ?, descripcion = ?, rutaImagen = ?, " +
                      "material = ?, categoria = ?, tipoPrenda = ?, estilo = ?, despCuidados = ?, disponibilidad = ?, " +
                      "idProveedor = ?, idMarca = ?, idTalla = ?, idColor = ?, idMedida = ? " + 
                      "WHERE idProducto = ?"; 
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int idx = 1;
            ps.setString(idx++, p.getNombre());
            ps.setDouble(idx++, p.getPrecioUni());
            ps.setString(idx++, p.getDescripcion());
            ps.setString(idx++, p.getRutaImagen());
            
            // --- Nuevos campos de atributos ---
            ps.setString(idx++, p.getMaterial());
            ps.setString(idx++, p.getCategoria());
            ps.setString(idx++, p.getTipoPrenda());
            ps.setString(idx++, p.getEstilo() != null ? p.getEstilo().name() : null);
            ps.setString(idx++, p.getDespCuidados());
            ps.setString(idx++, p.getDisponibilidad());
            
            // --- Claves Foráneas (long) ---
            ps.setLong(idx++, p.getIdProveedor());
            ps.setLong(idx++, p.getIdMarca());
            ps.setLong(idx++, p.getIdTalla());
            ps.setLong(idx++, p.getIdColor());
            ps.setLong(idx++, p.getIdMedida()); 
            
            ps.setLong(idx++, p.getIdProducto()); // Clave para WHERE
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // D - DELETE: ELIMINAR UN PRODUCTO
    public boolean eliminarProducto(long id) {
        String sql = "DELETE FROM productos WHERE idProducto = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ⭐ FUNCIÓN DE REPORTES CORREGIDA E IMPLEMENTADA ⭐
    public List<Producto> buscarProductosPorFiltro(
        String nombre, 
        Double precioMin, 
        Double precioMax, 
        String descripcion, 
        String material, 
        String categoria, 
        String estilo, 
        String disponibilidad
    ) {
        List<Producto> resultados = new ArrayList<>();
        
        // 1. Construcción dinámica del SQL
        StringBuilder sqlBuilder = new StringBuilder();
        // 1=1 permite construir la cláusula WHERE dinámicamente
        sqlBuilder.append("SELECT ").append(COLUMNAS_PRODUCTO).append(" FROM productos WHERE 1=1 "); 
        
        // Lista para almacenar los parámetros (valores) del PreparedStatement
        List<Object> parametros = new ArrayList<>();

        // Función auxiliar para verificar si un String es nulo o vacío después de trim
        // (La función Predicate se usa en la implementación para Java 8+)
        Predicate<String> isNotNullOrEmpty = s -> s != null && !s.trim().isEmpty();

        // FILTRO 1: Nombre (Búsqueda parcial LIKE)
        if (isNotNullOrEmpty.test(nombre)) {
            sqlBuilder.append(" AND nombre LIKE ? ");
            parametros.add("%" + nombre.trim() + "%");
        }

        // FILTRO 2: Descripción (Búsqueda parcial LIKE)
        if (isNotNullOrEmpty.test(descripcion)) {
            sqlBuilder.append(" AND descripcion LIKE ? ");
            parametros.add("%" + descripcion.trim() + "%");
        }

        // FILTRO 3: Precio Mínimo (Rango)
        if (precioMin != null) {
            sqlBuilder.append(" AND precioUni >= ? ");
            parametros.add(precioMin);
        }

        // FILTRO 4: Precio Máximo (Rango)
        if (precioMax != null) {
            sqlBuilder.append(" AND precioUni <= ? ");
            parametros.add(precioMax);
        }
        
        // FILTRO 5: Material (Búsqueda parcial LIKE)
        if (isNotNullOrEmpty.test(material)) {
            sqlBuilder.append(" AND material LIKE ? ");
            parametros.add("%" + material.trim() + "%");
        }

        // FILTRO 6: Categoría (Búsqueda parcial LIKE)
        if (isNotNullOrEmpty.test(categoria)) {
            sqlBuilder.append(" AND categoria LIKE ? ");
            parametros.add("%" + categoria.trim() + "%");
        }
        
        // FILTRO 7: Estilo (Búsqueda exacta)
        if (isNotNullOrEmpty.test(estilo)) {
            sqlBuilder.append(" AND estilo = ? ");
            parametros.add(estilo.trim());
        }
        
        // FILTRO 8: Disponibilidad (Búsqueda exacta)
        if (isNotNullOrEmpty.test(disponibilidad)) {
            sqlBuilder.append(" AND disponibilidad = ? ");
            parametros.add(disponibilidad.trim());
        }

        // Opcional: Ordenar los resultados
        sqlBuilder.append(" ORDER BY idProducto DESC");


        // 2. Ejecución de la consulta
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            
            // Asignar los parámetros dinámicamente al PreparedStatement
            for (int i = 0; i < parametros.size(); i++) {
                // Se usa setObject ya que los tipos son variados (String, Double)
                ps.setObject(i + 1, parametros.get(i));
            }

            // System.out.println("DEBUG SQL: " + sqlBuilder.toString()); // Descomentar para depuración
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearProducto(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por filtro: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    // ⭐ OTRO REPORTE (Ejemplo de Stock por Categoría) ⭐
    public Map<String, Integer> obtenerReporteStockPorCategoria() {
        Map<String, Integer> reporte = new HashMap<>();
        String sql = "SELECT p.categoria, SUM(i.cantidadStock) AS totalStock " +
                     "FROM productos p " +
                     "JOIN inventario i ON p.idProducto = i.idProducto " +
                     "GROUP BY p.categoria " +
                     "ORDER BY totalStock DESC";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String categoria = rs.getString("categoria");
                int totalStock = rs.getInt("totalStock");
                reporte.put(categoria, totalStock);
            }
        } catch (SQLException e) {
            System.err.println("Error al generar reporte de stock por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return reporte;
    }
}