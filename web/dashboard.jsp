<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.svcn.modelo.Administrador"%>
<%
    // Código para obtener el objeto Administrador de la sesión
    Administrador admin = (Administrador) session.getAttribute("usuarioLogueado");
    
    // El filtro ya protege esta página, pero si por alguna razón no se encuentra
    // el objeto, redirigimos como medida de seguridad adicional.
    if (admin == null) {
        response.sendRedirect("login.jsp");
        return; // Detener la ejecución del JSP
    }
    
    // Variables seguras para usar en la vista
    String nombreAdmin = (admin.getNombre() != null) ? admin.getNombre() : "Usuario";
    String rolAdmin = (admin.getRol() != null) ? admin.getRol() : "No definido";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVCN Style - Panel de Control</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;700&family=Oswald:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="admin.css"> 
    <link rel="icon" href="img/SVCN-Style-1.jpeg" type="image/x-icon">
    
    <style>
        .catalog-submenu {
            list-style: none;
            padding-left: 15px;
            margin-top: 5px;
            border-left: 2px solid #555; /* Estilo visual para separarlo */
        }
        .catalog-submenu a {
            font-size: 0.9em;
            padding: 5px 0;
            display: block;
        }
        /* Nueva clase para botones de catálogo que coincidan con tu tema */
        .catalog-btn {
            background-color: #A0522D; /* Color Café o similar al tema */
            color: white;
            border: 1px solid #8B4513;
            transition: background-color 0.2s;
        }
        .catalog-btn:hover {
            background-color: #8B4513;
            color: white;
        }
    </style>
</head>
<body>

    <header class="header">
        <div class="container header-content">
            <a href="index.jsp" class="logo">
                <img src="img/SVCN-Style-2.jpeg" alt="SVCN Logo" class="logo-img">
                SVCN <span class="logo-text-style">Style</span>
            </a>
            <nav class="nav-menu">
                <ul>
                    <li><a href="index.jsp" class="nav-link">Inicio</a></li>
                    <li><a href="products.jsp" class="nav-link">Productos</a></li>
                    <li class="nav-icon"><a href="LogoutServlet" class="nav-link">🚪</a></li> 
                </ul>
            </nav>
        </div>
    </header>

    <div class="dashboard-layout">
        <aside class="sidebar">
            <h4>Panel de Control</h4>
            <ul>
                <li><a href="dashboard.jsp" class="active">Inicio Panel</a></li>
                
                <li><a href="GestorProductosServlet?accion=listar">Gestión de Productos</a></li>
                <li><a href="GestorProveedoresServlet?accion=listar">Gestión de Proveedores</a></li>
                
                <li>
                    <a href="#" style="font-weight: bold;">Gestión de Catálogos</a>
                    <ul class="catalog-submenu">
                        <li><a href="GestorMarcasServlet?accion=listar">Marcas</a></li>
                        <li><a href="GestorTallasServlet?accion=listar">Tallas</a></li>
                        <li><a href="GestorColoresServlet?accion=listar">Colores</a></li>
                        <li><a href="GestorUnidadesMedidaServlet?accion=listar">Unidades de Medida</a></li>
                    </ul>
                </li>
                
                <li><a href="sales-form.jsp">Gestión de Ventas</a></li>
                <li><a href="ReportesServlet">Reportes</a></li>
                <li><a href="inventory.jsp">Inventario</a></li>
                <li><a href="settings.jsp">Configuración</a></li>
                <li><a href="LogoutServlet">Cerrar Sesión</a></li>
            </ul>
        </aside>
        
        <main class="main-content">
            <h2 class="section-title" style="text-align: left; padding-top: 0;">Bienvenido, <%= nombreAdmin %></h2>
            <p>Acceso rápido a las principales funcionalidades de administración. Tu rol es: **<%= rolAdmin %>**</p>

            <div class="dashboard-cards">
                
                <div class="dashboard-card">
                    <h3>Gestión de Productos</h3>
                    <p>Añade, edita o elimina productos del catálogo principal.</p>
                    <a href="GestorProductosServlet?accion=listar" class="button primary-button">Ir a Productos</a>
                </div>
                
                <div class="dashboard-card">
                    <h3>Gestión de Proveedores</h3>
                    <p>Administra la información de contacto de tus proveedores.</p>
                    <a href="GestorProveedoresServlet?accion=listar" class="button primary-button">Ir a Proveedores</a>
                </div>
                
                <div class="dashboard-card">
                    <h3>Catálogos (Marcas, Tallas, etc.)</h3>
                    <p>Define las opciones base para productos: Marcas, Tallas, Colores y Unidades.</p>
                    <div class="d-flex flex-wrap gap-2 mt-2">
                        <a href="GestorMarcasServlet?accion=listar" class="btn btn-sm catalog-btn">Marcas</a>
                        <a href="GestorTallasServlet?accion=listar" class="btn btn-sm catalog-btn">Tallas</a>
                        <a href="GestorColoresServlet?accion=listar" class="btn btn-sm catalog-btn">Colores</a>
                    </div>
                </div>

                <div class="dashboard-card">
                    <h3>Gestión de Ventas</h3>
                    <p>Administra pedidos, ventas y transacciones.</p>
                    <a href="sales-form.jsp" class="button primary-button">Ir a Ventas</a>
                </div>
                
                <div class="dashboard-card">
                    <h3>Reportes y Análisis</h3>
                    <p>Visualiza estadísticas y el rendimiento del negocio.</p>
                    <a href="ReportesServlet" class="button primary-button">Ver Reportes</a>
                </div>
                
                <div class="dashboard-card">
                    <h3>Control de Inventario</h3>
                    <p>Supervisa el stock actual y realiza movimientos.</p>
                    <a href="inventory.jsp" class="button primary-button">Ver Inventario</a>
                </div>
            </div>
        </main>
    </div>

    <footer class="footer">
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>