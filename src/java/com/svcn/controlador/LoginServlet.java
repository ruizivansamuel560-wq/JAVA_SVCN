package com.svcn.controlador;

import com.svcn.dao.AdministradorDAO;
import com.svcn.modelo.Administrador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Mapeo: El formulario HTML debe apuntar a esta URL
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // El formulario usa 'username' y 'password', pero tu DB usa 'correo' y 'contrasena'.
        // Usaremos 'correo' y 'contrasena' para coincidir con la DB, pero
        // usaremos los nombres de los inputs del formulario (username, password).
        
        String username = request.getParameter("username"); // Input del formulario
        String password = request.getParameter("password"); // Input del formulario

        AdministradorDAO adminDAO = new AdministradorDAO();
        
        // Llamamos al DAO usando los valores del formulario
        Administrador admin = adminDAO.validarLogin(username, password);

        if (admin != null) {
            // LOGIN EXITOSO
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", admin);
            session.setAttribute("rol", admin.getRol()); 
            
            // Redirigir al dashboard (Lo crearemos después)
            response.sendRedirect("dashboard.jsp"); 
            
        } else {
            // LOGIN FALLIDO
            
            // Adjuntar mensaje de error al request
            request.setAttribute("mensajeError", "Usuario o contraseña incorrectos.");
            
            // Re-enviar al login.jsp para mostrar el error
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}