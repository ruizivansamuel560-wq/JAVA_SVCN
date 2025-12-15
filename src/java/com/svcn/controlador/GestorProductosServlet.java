package com.svcn.controlador;

import com.svcn.dao.ProductoDAO;
import com.svcn.dao.ProveedorDAO;
import com.svcn.dao.MarcaDAO;
import com.svcn.dao.TallaDAO;
import com.svcn.dao.ColorDAO;
import com.svcn.dao.UnidadMedidaDAO;
import com.svcn.modelo.Administrador;
import com.svcn.modelo.Producto;
import com.svcn.modelo.Proveedor;
import com.svcn.modelo.Marca;
import com.svcn.modelo.Talla;
import com.svcn.modelo.Color;
import com.svcn.modelo.UnidadMedida;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map; // Necesario para el reporte

@WebServlet(name = "GestorProductosServlet", urlPatterns = {"/GestorProductosServlet", "/admin/GestorProductosServlet"})
// @MultipartConfig es necesario si se implementa la carga real de la imagen.
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class GestorProductosServlet extends HttpServlet {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    // Instancias de DAOs de Catálogo
    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final TallaDAO tallaDAO = new TallaDAO();
    private final ColorDAO colorDAO = new ColorDAO();
    private final UnidadMedidaDAO unidadMedidaDAO = new UnidadMedidaDAO();
    
    // --- Método auxiliar para cargar todas las listas de catálogo ---
    /**
     * Carga todas las listas de catálogos (FKs) y las establece como atributos de solicitud.
     */
    private void cargarCatalogos(HttpServletRequest request) {
        List<Proveedor> listaProveedores = proveedorDAO.listarProveedores();
        request.setAttribute("listaProveedores", listaProveedores);
        
        List<Marca> listaMarcas = marcaDAO.listarMarcas();
        request.setAttribute("listaMarcas", listaMarcas);
        
        List<Talla> listaTallas = tallaDAO.listarTallas();
        request.setAttribute("listaTallas", listaTallas);
        
        List<Color> listaColores = colorDAO.listarColores();
        request.setAttribute("listaColores", listaColores);
        
        List<UnidadMedida> listaUnidades = unidadMedidaDAO.listarUnidadesMedida();
        // Usamos "listaUnidades" para corresponder al campo 'idMedida' en el modelo
        request.setAttribute("listaUnidades", listaUnidades);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Cargar TODAS las listas necesarias para el JSP
        cargarCatalogos(request);
        
        // 2. Obtener la acción solicitada por el usuario
        String accion = request.getParameter("accion");
        
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarProductos(request, response);
                break;
            case "eliminar":
                eliminarProducto(request, response);
                break;
            case "editar":
                prepararEdicion(request, response);
                break;
            case "nuevo":
                prepararNuevo(request, response);
                break;
            case "reporteStock": // ⭐ Nueva acción para reportes
                verReporteStock(request, response);
                break;
            default:
                listarProductos(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        if ("guardar".equals(accion)) {
            guardarProducto(request, response);
        } else {
            // Si no es guardar, redirigimos al doGet para listar
            doGet(request, response);
        }
    }
    
    // ------------------- Métodos CRUD -------------------
    
    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Producto> listaProductos = productoDAO.listarProductos();
        
        request.setAttribute("listaProductos", listaProductos);
        
        request.getRequestDispatcher("productos-gestion.jsp").forward(request, response);
    }
    
    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            long id = Long.parseLong(request.getParameter("id"));
            boolean exito = productoDAO.eliminarProducto(id);
            
            // Usar Session para el mensaje flash
            request.getSession().setAttribute("mensajeFlash", exito ? "Producto eliminado exitosamente." : "Error al eliminar producto.");
        } catch (NumberFormatException e) {
             request.getSession().setAttribute("mensajeFlash", "ID de producto inválido para eliminar.");
        }
        
        response.sendRedirect("GestorProductosServlet?accion=listar");
    }
    
    private void prepararEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Producto producto = productoDAO.obtenerProductoPorId(id);
            
            request.setAttribute("productoAEditar", producto);
            request.getRequestDispatcher("productos-gestion.jsp").forward(request, response);
        } catch (NumberFormatException e) {
             request.setAttribute("mensajeError", "ID de producto inválido para edición.");
             listarProductos(request, response); // Si falla, volvemos a listar
        }
    }
    
    private void prepararNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("productoAEditar", new Producto());
        request.getRequestDispatcher("productos-gestion.jsp").forward(request, response);
    }

    private void guardarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener el ID para saber si es AGREGAR (id=0) o ACTUALIZAR (id > 0)
        long idProducto = 0;
        try {
            String idParam = request.getParameter("idProducto");
            if (idParam != null && !idParam.isEmpty()) {
                 idProducto = Long.parseLong(idParam);
            }
        } catch (NumberFormatException e) {
            // Si falla, se queda en 0 para intentar agregar uno nuevo
        }
        
        Producto p = new Producto();
        p.setIdProducto(idProducto);
        
        // Mapeo de campos de texto (incluyendo nuevos campos del modelo)
        p.setNombre(request.getParameter("nombre"));
        p.setDescripcion(request.getParameter("descripcion"));
        p.setRutaImagen(request.getParameter("rutaImagen"));
        p.setMaterial(request.getParameter("material"));
        p.setCategoria(request.getParameter("categoria"));
        p.setTipoPrenda(request.getParameter("tipoPrenda")); 
        p.setEstilo(request.getParameter("estilo")); // El modelo maneja la conversión a Enum
        p.setDespCuidados(request.getParameter("despCuidados"));
        p.setDisponibilidad(request.getParameter("disponibilidad"));
        
        // 2. Manejo de valores numéricos (Precio y FKs)
        try {
            // Precio (reemplazar coma por punto para el parseo)
            String precioStr = request.getParameter("precioUni");
            p.setPrecioUni(Double.parseDouble(precioStr.replace(",", ".")));
            
            // FKs (Todos son Longs)
            p.setIdProveedor(Long.parseLong(request.getParameter("idProveedor")));
            p.setIdMarca(Long.parseLong(request.getParameter("idMarca")));
            p.setIdTalla(Long.parseLong(request.getParameter("idTalla")));
            p.setIdColor(Long.parseLong(request.getParameter("idColor")));
            // Mapeo del parámetro 'idUnidad' a la propiedad 'idMedida' del modelo
            p.setIdMedida(Long.parseLong(request.getParameter("idUnidad"))); 

        } catch (NumberFormatException e) {
            // Si hay error, recargar el formulario con los datos ingresados y un mensaje.
            request.setAttribute("mensajeError", "Error: El precio o una clave foránea no tiene el formato numérico correcto. " + e.getMessage());
            request.setAttribute("productoAEditar", p);
            
            // Recargar listas de apoyo antes de hacer el forward
            cargarCatalogos(request);
            
            request.getRequestDispatcher("productos-gestion.jsp").forward(request, response);
            return;
        }
        
        // 3. Obtener ID del Admin logueado (Simulación si no hay sesión activa)
        HttpSession session = request.getSession();
        Administrador admin = (Administrador) session.getAttribute("usuarioLogueado");
        long idAdmin = admin != null ? admin.getIdAdmin() : 1; 
        p.setIdAdmin(idAdmin); 

        // 4. Llamar al DAO para guardar o actualizar
        boolean exito;
        String mensaje;
        if (idProducto == 0) {
            exito = productoDAO.agregarProducto(p);
            mensaje = exito ? "Producto agregado exitosamente." : "Error al agregar producto en la base de datos.";
        } else {
            exito = productoDAO.actualizarProducto(p);
            mensaje = exito ? "Producto actualizado exitosamente." : "Error al actualizar producto en la base de datos.";
        }
        
        session.setAttribute("mensajeFlash", mensaje);
        
        // 5. Redirigir a la lista
        response.sendRedirect("GestorProductosServlet?accion=listar"); 
    }
    
    // ------------------- Métodos de Reporte -------------------
    
    /**
     * Obtiene el reporte de stock por categoría y lo envía a una vista.
     */
    private void verReporteStock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el Map<Categoría, Stock Total> del DAO
        Map<String, Integer> reporteStock = productoDAO.obtenerReporteStockPorCategoria();
        
        // Subir el resultado al request
        request.setAttribute("reporteStock", reporteStock);
        
        // Redirigir a una vista específica para reportes (se asume que es productos-reporte-stock.jsp)
        request.getRequestDispatcher("productos-reporte-stock.jsp").forward(request, response);
    }
}