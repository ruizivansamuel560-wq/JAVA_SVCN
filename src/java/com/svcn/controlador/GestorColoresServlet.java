package com.svcn.controlador;

import com.svcn.dao.ColorDAO;
import com.svcn.modelo.Color;
import java.io.IOException;
import java.util.List;
// Usa 'jakarta.servlet' si usas Tomcat 10+ o Jakarta EE 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GestorColoresServlet", urlPatterns = {"/GestorColoresServlet"})
public class GestorColoresServlet extends HttpServlet {

    private final ColorDAO colorDAO = new ColorDAO();

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
                    listarColores(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditar(request, response);
                    break;
                case "eliminar":
                    eliminarColor(request, response, session);
                    break;
                default:
                    listarColores(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error en GestorColoresServlet (doGet): " + e.getMessage());
            request.setAttribute("mensajeError", "Ocurrió un error en el sistema.");
            listarColores(request, response);
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
            guardarColor(request, response, session);
        } else {
            response.sendRedirect("GestorColoresServlet?accion=listar");
        }
    }
    
    // --- MÉTODOS PRIVADOS DE GESTIÓN ---

    private void listarColores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Color> listaColores = colorDAO.listarColores();
        request.setAttribute("listaColores", listaColores);
        request.getRequestDispatcher("colores-gestion.jsp").forward(request, response);
    }
    
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("colorAEditar", new Color());
        listarColores(request, response);
    }
    
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Color color = colorDAO.obtenerColorPorId(id); // Asumiendo implementación en ColorDAO
        
        if (color != null) {
            request.setAttribute("colorAEditar", color);
        } else {
            request.setAttribute("mensajeError", "Color no encontrado con ID: " + id);
        }
        listarColores(request, response);
    }

    private void guardarColor(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int idColor = 0;
        try {
            idColor = Integer.parseInt(request.getParameter("idColor"));
        } catch (NumberFormatException e) { }
        
        String nombreColor = request.getParameter("nombreColor").trim();
        
        if (nombreColor.isEmpty()) {
             request.setAttribute("mensajeError", "El nombre del color no puede estar vacío.");
             Color c = new Color();
             c.setIdColor(idColor);
             request.setAttribute("colorAEditar", c);
             listarColores(request, response);
             return;
        }
        
        Color color = new Color();
        color.setIdColor(idColor);
        color.setNombreColor(nombreColor);
        
        boolean exito;
        String mensaje;

        if (idColor == 0) {
            exito = colorDAO.guardarColor(color);
            mensaje = exito ? "Color '" + nombreColor + "' registrado con éxito." : "Error: El nombre de color ya existe o falló la conexión.";
        } else {
            exito = colorDAO.actualizarColor(color); // Asumiendo implementación en ColorDAO
            mensaje = exito ? "Color '" + nombreColor + "' actualizado con éxito." : "Error al actualizar el color.";
        }
        
        if (!exito) {
             request.setAttribute("mensajeError", mensaje);
             request.setAttribute("colorAEditar", color);
             listarColores(request, response);
        } else {
            session.setAttribute("mensajeFlash", mensaje);
            response.sendRedirect("GestorColoresServlet?accion=listar");
        }
    }
    
    private void eliminarColor(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensajeFlash", "Error: ID de color inválido para eliminar.");
            response.sendRedirect("GestorColoresServlet?accion=listar");
            return;
        }
        
        Color color = colorDAO.obtenerColorPorId(id); // Asumiendo implementación en ColorDAO
        String nombreColor = (color != null) ? color.getNombreColor() : "ID: " + id;
        
        boolean exito = colorDAO.eliminarColor(id);
        
        String mensaje = exito ? "Color '" + nombreColor + "' eliminado con éxito." : "Error al eliminar el color. Podría tener productos asociados u otro fallo de DB.";
        
        session.setAttribute("mensajeFlash", mensaje);
        response.sendRedirect("GestorColoresServlet?accion=listar");
    }
}