<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Registro de Administrador - SVCN Style</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="login.css"/> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <%
        // Obtener mensajes de error o éxito del Servlet
        String mensajeError = (String) request.getAttribute("mensajeError");
        String mensajeExito = (String) request.getAttribute("mensajeExito");
        
        // Mantener los datos en caso de error para usabilidad
        String nombre = (String) request.getAttribute("nombreRecuperado");
        String email = (String) request.getAttribute("emailRecuperado");
        String telefono = (String) request.getAttribute("telefonoRecuperado");
    %>

    <div class="login-container">
        <div class="login-box" style="height: auto; max-width: 450px;">
            <div class="login-header">
                <a href="index.jsp" class="login-logo-link">
                    <img src="img/SVCN-Style-2.jpeg" alt="SVCN Logo" class="login-logo-img"/>
                    SVCN <span class="login-logo-text-style">Style</span>
                </a>
                <h2>Registro de Administrador</h2>
            </div>
            
            <% if (mensajeError != null) { %>
                <div class="error-message" style="display: block;"><%= mensajeError %></div>
            <% } else if (mensajeExito != null) { %>
                <div class="success-message" style="display: block; color: green; border-color: green;">
                    <%= mensajeExito %>
                </div>
            <% } %>

            <form class="login-form" action="RegistroAdminServlet" method="POST">
                
                <div class="input-group">
                    <i class="fas fa-id-badge icon"></i>
                    <input type="text" id="nombre" name="nombre" placeholder="Nombre Completo" 
                           value="<%= nombre != null ? nombre : "" %>" required/>
                </div>
                
                <div class="input-group">
                    <i class="fas fa-envelope icon"></i>
                    <input type="email" id="email" name="email" placeholder="Correo Electrónico" 
                           value="<%= email != null ? email : "" %>" required/>
                </div>
                
                <div class="input-group">
                    <i class="fas fa-phone icon"></i>
                    <input type="text" id="telefono" name="telefono" placeholder="Teléfono" 
                           value="<%= telefono != null ? telefono : "" %>" required/>
                </div>

                <div class="input-group">
                    <i class="fas fa-lock icon"></i>
                    <input type="password" id="password" name="password" placeholder="Contraseña" required/>
                </div>
                
                <button type="submit" class="login-button">Registrar Administrador</button>
            </form>
            
            <div class="login-footer">
                <p>¿Ya tienes una cuenta? <a href="login.jsp" class="register-link">Inicia Sesión aquí</a></p>
            </div>
        </div>
    </div>

</body>
</html>