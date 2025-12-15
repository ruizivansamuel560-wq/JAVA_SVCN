package com.svcn.controlador;

import com.svcn.dao.MarcaDAO;
import com.svcn.modelo.Marca;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GestorMarcasServlet", urlPatterns = {"/GestorMarcasServlet"})
public class GestorMarcasServlet extends HttpServlet {

    private final MarcaDAO marcaDAO = new MarcaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Sesión de Administrador
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
                    listarMarcas(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditar(request, response);
                    break;
                case "eliminar":
                    eliminarMarca(request, response, session);
                    break;
                default:
                    listarMarcas(request, response);
            }
        } catch (Exception e) {
            // Manejo de errores genérico
            System.err.println("Error en GestorMarcasServlet (doGet): " + e.getMessage());
            request.setAttribute("mensajeError", "Ocurrió un error en el sistema: " + e.getMessage());
            listarMarcas(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Sesión de Administrador
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String accion = request.getParameter("accion");
        if ("guardar".equals(accion)) {
            // El método guardarMarca ahora maneja las excepciones internamente
            guardarMarca(request, response, session);
        } else {
            // Si POST no es 'guardar', redirigir a listar
            response.sendRedirect("GestorMarcasServlet?accion=listar");
        }
    }
    
    // --- MÉTODOS PRIVADOS DE GESTIÓN ---

    private void listarMarcas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Marca> listaMarcas = marcaDAO.listarMarcas();
        request.setAttribute("listaMarcas", listaMarcas);
        
        // Usar RequestDispatcher para incluir el JSP
        request.getRequestDispatcher("marcas-gestion.jsp").forward(request, response);
    }
    
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Inicializar una marca vacía para activar el modo "creación" en el JSP (ID=0)
        request.setAttribute("marcaAEditar", new Marca());
        listarMarcas(request, response); // Muestra el listado y el formulario
    }
    
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Marca marca = marcaDAO.obtenerMarcaPorId(id);
        
        if (marca != null) {
            request.setAttribute("marcaAEditar", marca);
        } else {
            request.setAttribute("mensajeError", "Marca no encontrada con ID: " + id);
        }
        listarMarcas(request, response); // Muestra el listado y el formulario
    }

    private void guardarMarca(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        // Obtener parámetros
        int idMarca = 0;
        try {
            idMarca = Integer.parseInt(request.getParameter("idMarca"));
        } catch (NumberFormatException e) {
            // Manejar si idMarca no es un número (aunque el input hidden lo impide normalmente)
        }
        
        String nombreMarca = request.getParameter("nombreMarca").trim();
        
        if (nombreMarca.isEmpty()) {
             request.setAttribute("mensajeError", "El nombre de la marca no puede estar vacío.");
             Marca m = new Marca();
             m.setIdMarca(idMarca);
             request.setAttribute("marcaAEditar", m);
             listarMarcas(request, response);
             return;
        }
        
        Marca marca = new Marca();
        marca.setIdMarca(idMarca);
        marca.setNombreMarca(nombreMarca);
        
        boolean exito;
        String mensaje;

        if (idMarca == 0) {
            // Creación
            exito = marcaDAO.guardarMarca(marca);
            mensaje = exito ? "Marca '" + nombreMarca + "' registrada con éxito." : "Error: El nombre de marca ya existe o falló la conexión.";
        } else {
            // Edición
            exito = marcaDAO.actualizarMarca(marca);
            mensaje = exito ? "Marca '" + nombreMarca + "' actualizada con éxito." : "Error al actualizar la marca.";
        }
        
        // Si no hay éxito en la creación o actualización, volvemos a mostrar el formulario con error
        if (!exito) {
             request.setAttribute("mensajeError", mensaje);
             request.setAttribute("marcaAEditar", marca);
             listarMarcas(request, response);
        } else {
            // Redirigir para evitar doble submission y usar mensaje flash
            session.setAttribute("mensajeFlash", mensaje);
            response.sendRedirect("GestorMarcasServlet?accion=listar");
        }
    }
    
    private void eliminarMarca(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            session.setAttribute("mensajeFlash", "Error: ID de marca inválido para eliminar.");
            response.sendRedirect("GestorMarcasServlet?accion=listar");
            return;
        }
        
        Marca marca = marcaDAO.obtenerMarcaPorId(id);
        String nombreMarca = (marca != null) ? marca.getNombreMarca() : "ID: " + id;
        
        boolean exito = marcaDAO.eliminarMarca(id);
        
        // Se añade un mensaje de precaución si falla la eliminación
        String mensaje = exito ? "Marca '" + nombreMarca + "' eliminada con éxito." : "Error al eliminar la marca. Podría tener productos asociados u otro fallo de DB.";
        
        session.setAttribute("mensajeFlash", mensaje);
        response.sendRedirect("GestorMarcasServlet?accion=listar");
    }
}