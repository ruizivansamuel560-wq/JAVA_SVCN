package com.svcn.controlador;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Mapeamos el filtro para que se aplique a todas las URLs que comiencen con /admin/
// Usaremos el patrón /admin/* para todas las páginas del panel
// También lo aplicaremos a dashboard.jsp por si acaso
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/dashboard.jsp", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        // Convertir request y response a sus versiones HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Obtener la sesión
        HttpSession session = httpRequest.getSession(false); // No crea una nueva si no existe
        
        // Comprobar si el usuario ha iniciado sesión (buscando el atributo)
        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogueado") != null);

        // Si el usuario NO está logueado, redirigir a la página de login
        if (!isLoggedIn) {
            // Guardar un mensaje de error opcional
            httpRequest.setAttribute("mensajeError", "Necesitas iniciar sesión para acceder al panel.");
            
            // Redirigir a login.jsp
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            
        } else {
            // Si el usuario está logueado, permitir el acceso a la página solicitada
            chain.doFilter(request, response);
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización, generalmente vacío
    }
    
    @Override
    public void destroy() {
        // Limpieza, generalmente vacío
    }
}