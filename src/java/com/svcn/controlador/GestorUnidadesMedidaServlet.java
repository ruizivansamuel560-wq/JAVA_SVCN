package com.svcn.controlador;

import com.svcn.dao.UnidadMedidaDAO;
import com.svcn.modelo.UnidadMedida;
import java.io.IOException;
import java.util.List;
// Usa 'jakarta.servlet' si usas Tomcat 10+ o Jakarta EE 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GestorUnidadesMedidaServlet", urlPatterns = {"/GestorUnidadesMedidaServlet"})
public class GestorUnidadesMedidaServlet extends HttpServlet {

    private final UnidadMedidaDAO unidadMedidaDAO = new UnidadMedidaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "listar":
                    listarUnidadesMedida(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditar(request, response);
                    break;
                case "eliminar":
                    eliminarUnidadMedida(request, response, session);
                    break;
                default:
                    listarUnidadesMedida(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error en GestorUnidadesMedidaServlet (doGet): " + e.getMessage());
            e.printStackTrace(); // Es útil ver la traza completa
            request.setAttribute("mensajeError", "Ocurrió un error en el sistema: " + e.getMessage());
            listarUnidadesMedida(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String accion = request.getParameter("accion");
        if ("guardar".equals(accion)) {
            guardarUnidadMedida(request, response, session);
        } else {
            response.sendRedirect("GestorUnidadesMedidaServlet?accion=listar");
        }
    }
    
    // --- MÉTODOS PRIVADOS DE GESTIÓN ---

    private void listarUnidadesMedida(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<UnidadMedida> listaUnidadesMedida = unidadMedidaDAO.listarUnidadesMedida();
        // CLAVE: El JSP espera 'listaUnidades', no 'listaUnidadesMedida'
        request.setAttribute("listaUnidades", listaUnidadesMedida); 
        request.getRequestDispatcher("unidadesmedida-gestion.jsp").forward(request, response);
    }
    
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // CLAVE: El JSP espera 'unidadAEditar', no 'unidadMedidaAEditar'
        request.setAttribute("unidadAEditar", new UnidadMedida()); 
        listarUnidadesMedida(request, response);
    }
    
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = 0;
        try {
             id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
             request.setAttribute("mensajeError", "Error: ID de unidad de medida inválido para editar.");
             listarUnidadesMedida(request, response);
             return;
        }

        UnidadMedida unidadMedida = unidadMedidaDAO.obtenerUnidadMedidaPorId(id); 
        
        if (unidadMedida != null) {
            // CLAVE: El JSP espera 'unidadAEditar'
            request.setAttribute("unidadAEditar", unidadMedida); 
        } else {
            request.setAttribute("mensajeError", "Unidad de Medida no encontrada con ID: " + id);
        }
        listarUnidadesMedida(request, response);
    }

    private void guardarUnidadMedida(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        // 1. Obtener parámetros
        int idUnidad = 0; // CLAVE: Cambiamos idMedida a idUnidad (lo que el JSP envía)
        try {
            // CLAVE: Obtenemos "idUnidad" del hidden field del formulario (aunque el formulario lo envía como idUnidad)
            String idStr = request.getParameter("idUnidad"); 
            if (idStr != null && !idStr.isEmpty()) {
                idUnidad = Integer.parseInt(idStr);
            }
        } catch (NumberFormatException e) { /* idUnidad = 0 si falla */ }
        
        // CLAVE: El JSP envía 'nombre' y 'abreviacion', no 'nombreMedida'
        String nombre = request.getParameter("nombre") != null ? request.getParameter("nombre").trim() : "";
        String abreviacion = request.getParameter("abreviacion") != null ? request.getParameter("abreviacion").trim() : "";
        
        // 2. Validación
        if (nombre.isEmpty() || abreviacion.isEmpty()) {
             request.setAttribute("mensajeError", "El nombre y la abreviación de la unidad de medida no pueden estar vacíos.");
             UnidadMedida um = new UnidadMedida();
             um.setIdUnidad(idUnidad); // CLAVE: Usamos setIdUnidad
             um.setNombre(nombre); // CLAVE: Usamos setNombre
             um.setAbreviacion(abreviacion); // CLAVE: Usamos setAbreviacion
             request.setAttribute("unidadAEditar", um); // CLAVE: El JSP espera 'unidadAEditar'
             listarUnidadesMedida(request, response);
             return;
        }
        
        // 3. Crear o actualizar objeto
        UnidadMedida unidadMedida = new UnidadMedida();
        unidadMedida.setIdUnidad(idUnidad); // CLAVE: Usamos setIdUnidad
        unidadMedida.setNombre(nombre); // CLAVE: Usamos setNombre
        unidadMedida.setAbreviacion(abreviacion); // CLAVE: Usamos setAbreviacion
        
        // 4. Ejecutar DAO
        boolean exito;
        String mensaje;

        if (idUnidad == 0) {
            exito = unidadMedidaDAO.guardarUnidadMedida(unidadMedida);
            mensaje = exito ? "Unidad de Medida '" + nombre + "' registrada con éxito." : "Error: El nombre de la unidad de medida ya existe o falló la conexión.";
        } else {
            exito = unidadMedidaDAO.actualizarUnidadMedida(unidadMedida); 
            mensaje = exito ? "Unidad de Medida '" + nombre + "' actualizada con éxito." : "Error al actualizar la unidad de medida.";
        }
        
        // 5. Redirección y manejo de errores
        if (!exito) {
             request.setAttribute("mensajeError", mensaje);
             request.setAttribute("unidadAEditar", unidadMedida); // CLAVE: El JSP espera 'unidadAEditar'
             listarUnidadesMedida(request, response);
        } else {
            session.setAttribute("mensajeFlash", mensaje);
            response.sendRedirect("GestorUnidadesMedidaServlet?accion=listar");
        }
    }
    
    private void eliminarUnidadMedida(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensajeFlash", "Error: ID de unidad de medida inválido para eliminar.");
            response.sendRedirect("GestorUnidadesMedidaServlet?accion=listar");
            return;
        }
        
        UnidadMedida unidadMedida = unidadMedidaDAO.obtenerUnidadMedidaPorId(id); 
        // CLAVE: Usamos getNombre()
        String nombre = (unidadMedida != null) ? unidadMedida.getNombre() : "ID: " + id; 
        
        boolean exito = unidadMedidaDAO.eliminarUnidadMedida(id);
        
        String mensaje = exito ? "Unidad de Medida '" + nombre + "' eliminada con éxito." : "Error al eliminar la unidad de medida. Podría tener productos asociados u otro fallo de DB.";
        
        session.setAttribute("mensajeFlash", mensaje);
        response.sendRedirect("GestorUnidadesMedidaServlet?accion=listar");
    }
}