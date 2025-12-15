package com.svcn.controlador;

import com.svcn.dao.ProductoDAO;
import com.svcn.modelo.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportesServlet", urlPatterns = {"/ReportesServlet", "/admin/ReportesServlet"})
public class ReportesServlet extends HttpServlet {

    private ProductoDAO productoDAO = new ProductoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Simplemente redirigimos a la vista de reportes sin datos iniciales
        // La vista mostrará el formulario de filtro vacío al principio.
        request.getRequestDispatcher("reportes-productos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener los parámetros del formulario de filtro
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        String precioMinStr = request.getParameter("precioMin");
        String precioMaxStr = request.getParameter("precioMax");
        
        // --- OBTENER NUEVOS FILTROS ---
        String material = request.getParameter("material");
        String categoria = request.getParameter("categoria");
        String estilo = request.getParameter("estilo");
        String disponibilidad = request.getParameter("disponibilidad");
        // --- FIN: OBTENER NUEVOS FILTROS ---
        
        Double precioMin = null;
        Double precioMax = null;

        // Intentar parsear los precios (manejo de errores/nulls)
        try {
            if (precioMinStr != null && !precioMinStr.trim().isEmpty()) {
                // Usamos replace(",", ".") para aceptar el formato de decimales con coma
                precioMin = Double.parseDouble(precioMinStr.replace(",", "."));
            }
            if (precioMaxStr != null && !precioMaxStr.trim().isEmpty()) {
                precioMax = Double.parseDouble(precioMaxStr.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "Formato de precio inválido. Por favor, ingrese solo números.");
            
            // Mantener los filtros en caso de error para que el usuario no pierda la información
            request.setAttribute("filtroNombre", nombre);
            request.setAttribute("filtroDescripcion", descripcion);
            request.setAttribute("filtroPrecioMin", precioMinStr);
            request.setAttribute("filtroPrecioMax", precioMaxStr);
            request.setAttribute("filtroMaterial", material);
            request.setAttribute("filtroCategoria", categoria);
            request.setAttribute("filtroEstilo", estilo);
            request.setAttribute("filtroDisponibilidad", disponibilidad);
            
            request.getRequestDispatcher("reportes-productos.jsp").forward(request, response);
            return; 
        }
        
        // 2. Ejecutar la consulta con los filtros
        List<Producto> resultados = productoDAO.buscarProductosPorFiltro(
                nombre, precioMin, precioMax, descripcion, 
                material, categoria, estilo, disponibilidad
        );
        
        // 3. Guardar resultados y parámetros de filtro para la vista
        request.setAttribute("resultadosReporte", resultados);
        
        // Guardar los parámetros para que el formulario se mantenga lleno (usabilidad)
        request.setAttribute("filtroNombre", nombre);
        request.setAttribute("filtroDescripcion", descripcion);
        request.setAttribute("filtroPrecioMin", precioMinStr);
        request.setAttribute("filtroPrecioMax", precioMaxStr);
        
        // --- GUARDAR NUEVOS FILTROS ---
        request.setAttribute("filtroMaterial", material);
        request.setAttribute("filtroCategoria", categoria);
        request.setAttribute("filtroEstilo", estilo);
        request.setAttribute("filtroDisponibilidad", disponibilidad);
        // --- FIN GUARDAR NUEVOS FILTROS ---
        
        // 4. Redirigir a la vista de reportes
        request.getRequestDispatcher("reportes-productos.jsp").forward(request, response);
    }
}