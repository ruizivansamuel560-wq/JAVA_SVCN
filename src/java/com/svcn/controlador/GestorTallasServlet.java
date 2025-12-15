package com.svcn.controlador;

import com.svcn.dao.TallaDAO;
import com.svcn.modelo.Talla;
import java.io.IOException;
import java.util.List;
// Usa 'jakarta.servlet' si usas Tomcat 10+ o Jakarta EE 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GestorTallasServlet", urlPatterns = {"/GestorTallasServlet"})
public class GestorTallasServlet extends HttpServlet {

    private final TallaDAO tallaDAO = new TallaDAO();

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
                    listarTallas(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditar(request, response);
                    break;
                case "eliminar":
                    eliminarTalla(request, response, session);
                    break;
                default:
                    listarTallas(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error en GestorTallasServlet (doGet): " + e.getMessage());
            request.setAttribute("mensajeError", "Ocurrió un error en el sistema.");
            listarTallas(request, response);
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
            guardarTalla(request, response, session);
        } else {
            response.sendRedirect("GestorTallasServlet?accion=listar");
        }
    }
    
    // --- MÉTODOS PRIVADOS DE GESTIÓN ---

    private void listarTallas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Talla> listaTallas = tallaDAO.listarTallas();
        request.setAttribute("listaTallas", listaTallas);
        request.getRequestDispatcher("tallas-gestion.jsp").forward(request, response);
    }
    
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("tallaAEditar", new Talla());
        listarTallas(request, response);
    }
    
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Talla talla = tallaDAO.obtenerTallaPorId(id); // Asumiendo que has implementado este método en TallaDAO
        
        if (talla != null) {
            request.setAttribute("tallaAEditar", talla);
        } else {
            request.setAttribute("mensajeError", "Talla no encontrada con ID: " + id);
        }
        listarTallas(request, response);
    }

    private void guardarTalla(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int idTalla = 0;
        try {
            idTalla = Integer.parseInt(request.getParameter("idTalla"));
        } catch (NumberFormatException e) { }
        
        String nombreTalla = request.getParameter("nombreTalla").trim();
        
        if (nombreTalla.isEmpty()) {
             request.setAttribute("mensajeError", "El nombre de la talla no puede estar vacío.");
             Talla t = new Talla();
             t.setIdTalla(idTalla);
             request.setAttribute("tallaAEditar", t);
             listarTallas(request, response);
             return;
        }
        
        Talla talla = new Talla();
        talla.setIdTalla(idTalla);
        talla.setNombreTalla(nombreTalla);
        
        boolean exito;
        String mensaje;

        if (idTalla == 0) {
            exito = tallaDAO.guardarTalla(talla);
            mensaje = exito ? "Talla '" + nombreTalla + "' registrada con éxito." : "Error: La talla ya existe o falló la conexión.";
        } else {
            exito = tallaDAO.actualizarTalla(talla); // Asumiendo que has implementado este método en TallaDAO
            mensaje = exito ? "Talla '" + nombreTalla + "' actualizada con éxito." : "Error al actualizar la talla.";
        }
        
        if (!exito) {
             request.setAttribute("mensajeError", mensaje);
             request.setAttribute("tallaAEditar", talla);
             listarTallas(request, response);
        } else {
            session.setAttribute("mensajeFlash", mensaje);
            response.sendRedirect("GestorTallasServlet?accion=listar");
        }
    }
    
    private void eliminarTalla(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensajeFlash", "Error: ID de talla inválido para eliminar.");
            response.sendRedirect("GestorTallasServlet?accion=listar");
            return;
        }
        
        Talla talla = tallaDAO.obtenerTallaPorId(id); // Asumiendo que has implementado este método en TallaDAO
        String nombreTalla = (talla != null) ? talla.getNombreTalla() : "ID: " + id;
        
        boolean exito = tallaDAO.eliminarTalla(id);
        
        String mensaje = exito ? "Talla '" + nombreTalla + "' eliminada con éxito." : "Error al eliminar la talla. Podría tener productos asociados u otro fallo de DB.";
        
        session.setAttribute("mensajeFlash", mensaje);
        response.sendRedirect("GestorTallasServlet?accion=listar");
    }
}