package com.svcn.controlador;

import com.svcn.dao.AdministradorDAO;
import com.svcn.modelo.Administrador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Mapeamos el Servlet a la URL que usa el formulario
@WebServlet(name = "RegistroAdminServlet", urlPatterns = {"/RegistroAdminServlet"})
public class RegistroAdminServlet extends HttpServlet {

    private final AdministradorDAO administradorDAO = new AdministradorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Simplemente redirige al formulario de registro
        request.getRequestDispatcher("registro-admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener parámetros del formulario
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password"); // Variable que contiene la contraseña
        
        // 2. Almacenar temporalmente los datos para recuperar en caso de error
        request.setAttribute("nombreRecuperado", nombre);
        request.setAttribute("emailRecuperado", email);
        request.setAttribute("telefonoRecuperado", telefono);

        // 3. Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            telefono == null || telefono.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            
            request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registro-admin.jsp").forward(request, response);
            return;
        }

        // 4. Crear el objeto Administrador
        Administrador nuevoAdmin = new Administrador();
        nuevoAdmin.setNombre(nombre);
        nuevoAdmin.setCorreo(email);
        nuevoAdmin.setTelefono(telefono);
        
        // Asignar el rol fijo de ADMINISTRADOR 
        nuevoAdmin.setRol("ADMINISTRADOR"); 
        
        // ¡LÍNEA CORREGIDA! Usamos la variable 'password' que obtuvimos del formulario.
        nuevoAdmin.setContrasena(password); 

        // 5. Intentar registrar el administrador
        try {
            if (administradorDAO.agregarAdministrador(nuevoAdmin)) {
                request.setAttribute("mensajeExito", "Registro exitoso. Ya puedes iniciar sesión.");
                
                // Limpiar campos recuperados tras el éxito
                request.removeAttribute("nombreRecuperado");
                request.removeAttribute("emailRecuperado");
                request.removeAttribute("telefonoRecuperado");
                
                // Redirigir al login 
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                // Posible error de DB (ej. email duplicado)
                request.setAttribute("mensajeError", "Error al registrar. El correo electrónico podría ya estar en uso o hubo un fallo en el sistema.");
                request.getRequestDispatcher("registro-admin.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error fatal en el registro: " + e.getMessage());
            request.setAttribute("mensajeError", "Ocurrió un error inesperado. Inténtelo de nuevo.");
            request.getRequestDispatcher("registro-admin.jsp").forward(request, response);
        }
    }
}