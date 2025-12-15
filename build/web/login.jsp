<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Iniciar Sesión - SVCN Style</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="login.css"/> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <a href="index.jsp" class="login-logo-link">           <img src="img/SVCN-Style-2.jpeg" alt="SVCN Logo" class="login-logo-img"/>           SVCN <span class="login-logo-text-style">Style</span>
        </a>
        <h2>Iniciar Sesión</h2>
      </div>
              <form class="login-form" action="LoginServlet" method="POST">
        <div class="input-group">
          <i class="fas fa-user icon"></i>
                    <input type="text" id="username" name="username" placeholder="Usuario (Correo)" required/>
        </div>
        <div class="input-group">
          <i class="fas fa-lock icon"></i>
                    <input type="password" id="password" name="password" placeholder="Contraseña" required/>
        </div>
        
        <%
            String error = (String) request.getAttribute("mensajeError");
            if (error != null) {
        %>
                <div id="errorMessage" class="error-message" style="display: block;"><%= error %></div>
        <%
            } else {
        %>
                <div id="errorMessage" class="error-message"></div>
        <%
            }
        %>
        
        <div class="remember-me">
          <input type="checkbox" id="remember" name="remember"/>
          <label for="remember">Recordarme</label>
        </div>
        <button type="submit" class="login-button">Entrar</button>
      </form>
      <div class="login-footer">
        <a href="#" class="forgot-password-link">¿Olvidaste tu contraseña?</a>
        <p>¿No tienes una cuenta? <a href="registro-admin.jsp" class="register-link">Regístrate aquí</a></p>       </div>
    </div>
  </div>

  </body>
</html>