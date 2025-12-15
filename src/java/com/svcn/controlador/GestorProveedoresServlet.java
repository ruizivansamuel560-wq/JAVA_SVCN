package com.svcn.controlador;

import com.svcn.dao.ProveedorDAO;
import com.svcn.modelo.Proveedor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet controlador para la gestión de la entidad Proveedor.
 * Maneja las acciones de listar, nuevo, editar, guardar y eliminar.
 */
@WebServlet(name = "GestorProveedoresServlet", urlPatterns = {"/GestorProveedoresServlet"})
public class GestorProveedoresServlet extends HttpServlet {

    private ProveedorDAO proveedorDao;

    @Override
    public void init() throws ServletException {
        // Inicializa el DAO al iniciar el Servlet
        this.proveedorDao = new ProveedorDAO(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Control de acceso
        if (request.getSession().getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // Acción por defecto
        }

        try {
            switch (accion) {
                case "listar":
                    listarProveedores(request, response);
                    break;
                case "nuevo":
                    mostrarFormulario(request, response, true);
                    break;
                case "editar":
                    obtenerProveedorParaEditar(request, response);
                    break;
                case "eliminar":
                    eliminarProveedor(request, response);
                    break;
                default:
                    listarProveedores(request, response);
                    break;
            }
        } catch (Exception e) {
            // Loguear el error y mostrar un error general al usuario
            e.printStackTrace(); 
            throw new ServletException("Error crítico en GestorProveedoresServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if ("guardar".equals(accion)) {
            guardarProveedor(request, response);
        } else {
             response.sendRedirect("GestorProveedoresServlet?accion=listar");
        }
    }

    // --- IMPLEMENTACIÓN DE ACCIONES ---

    private void listarProveedores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Proveedor> listaProveedores = proveedorDao.listarProveedores();
        request.setAttribute("listaProveedores", listaProveedores);
        
        // Cargar el JSP de gestión
        request.getRequestDispatcher("proveedores-gestion.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, boolean esNuevo)
            throws ServletException, IOException {
        
        // Usar constructor por defecto (gracias a la corrección en Proveedor.java)
        Proveedor proveedor = new Proveedor(); 
        
        if (esNuevo) {
            proveedor.setIdProveedor(0); 
        }

        request.setAttribute("proveedorAEditar", proveedor);
        
        // Reutilizar listarProveedores para cargar también la lista y mostrar el formulario
        listarProveedores(request, response); 
    }
    
    private void obtenerProveedorParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            long idProveedor = Long.parseLong(request.getParameter("id"));
            Proveedor proveedorAEditar = proveedorDao.obtenerProveedorPorId(idProveedor);
            
            HttpSession session = request.getSession();

            if (proveedorAEditar != null) {
                request.setAttribute("proveedorAEditar", proveedorAEditar);
                // Cargar la lista de proveedores para que la tabla siga visible
                listarProveedores(request, response); 
            } else {
                session.setAttribute("mensajeFlash", "Error: Proveedor con ID " + idProveedor + " no encontrado.");
                response.sendRedirect("GestorProveedoresServlet?accion=listar");
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("mensajeFlash", "Error: ID de proveedor inválido.");
            response.sendRedirect("GestorProveedoresServlet?accion=listar");
        }
    }

    private void guardarProveedor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recoger ID (manejo de caso de edición)
        long idProveedor = 0;
        try {
            String idParam = request.getParameter("idProveedor");
            if (idParam != null && !idParam.isEmpty()) {
                idProveedor = Long.parseLong(idParam);
            }
        } catch (NumberFormatException e) {
            // Esto no debería suceder si el JSP está bien, pero aseguramos id=0 si falla.
        }
        
        // 2. Recoger Parámetros del Formulario (JSP)
        // NOTA: Usamos getParameter().trim() para limpiar espacios.
        String nombreProv = request.getParameter("nombreProv") != null ? request.getParameter("nombreProv").trim() : "";
        String telefono = request.getParameter("telefono") != null ? request.getParameter("telefono").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String direccion = request.getParameter("direccion") != null ? request.getParameter("direccion").trim() : "";
        
        
        // 3. Crear objeto Proveedor y mapear los datos del formulario al modelo
        Proveedor p = new Proveedor();
        p.setIdProveedor(idProveedor);
        p.setNombreProv(nombreProv);
        
        // MAPEO CRÍTICO: Formulario JSP -> Modelo Proveedor
        p.setContacto(telefono);    // Formulario 'telefono' -> Modelo 'contacto'
        p.setCorreo(email);         // Formulario 'email'    -> Modelo 'correo'
        p.setUbicacion(direccion);  // Formulario 'direccion' -> Modelo 'ubicacion'
        
        // 4. Validar datos
        if (nombreProv.isEmpty()) {
            request.setAttribute("mensajeError", "El nombre del proveedor es obligatorio.");
            request.setAttribute("proveedorAEditar", p); // Devolvemos el objeto para rellenar el formulario
            listarProveedores(request, response); // Mostrar el formulario con el error
            return;
        }

        // 5. Llamar al DAO para Guardar/Actualizar
        boolean resultado;
        String mensajeAccion = (idProveedor == 0) ? "guardado" : "actualizado";
        
        if (idProveedor == 0) {
            resultado = proveedorDao.guardarProveedor(p); 
        } else {
            resultado = proveedorDao.actualizarProveedor(p); 
        }

        // 6. Manejo de respuesta y Redirección (PRG Pattern)
        HttpSession session = request.getSession();
        if (resultado) {
            session.setAttribute("mensajeFlash", "Proveedor " + mensajeAccion + " exitosamente.");
        } else {
            session.setAttribute("mensajeFlash", "Error al " + mensajeAccion + " el proveedor. Verifique la base de datos o claves duplicadas.");
        }

        response.sendRedirect("GestorProveedoresServlet?accion=listar");
    }

    private void eliminarProveedor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            long idProveedor = Long.parseLong(request.getParameter("id"));
            
            boolean eliminado = proveedorDao.eliminarProveedor(idProveedor);
            
            HttpSession session = request.getSession();
            if (eliminado) {
                session.setAttribute("mensajeFlash", "Proveedor eliminado correctamente.");
            } else {
                session.setAttribute("mensajeFlash", "Error: No se pudo eliminar el proveedor (posiblemente esté relacionado con productos).");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("mensajeFlash", "Error: ID de proveedor inválido para la eliminación.");
        }

        response.sendRedirect("GestorProveedoresServlet?accion=listar");
    }
}