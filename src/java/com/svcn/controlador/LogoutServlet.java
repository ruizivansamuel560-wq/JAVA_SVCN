package com.svcn.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener la sesión (false para no crearla si no existe)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Invalidar la sesión (eliminar todos los atributos, incluyendo el usuario)
            session.invalidate();
        }
        
        // Redirigir al usuario a la página de inicio o login
        response.sendRedirect("login.jsp");
    }
}