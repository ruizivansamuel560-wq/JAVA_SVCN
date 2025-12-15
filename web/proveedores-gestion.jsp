<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.svcn.modelo.Proveedor"%>
<%@page import="com.svcn.modelo.Administrador"%>
<%@page import="java.util.List"%>
<%
    // Lógica JSP de configuración y obtención de atributos
    Administrador admin = (Administrador) session.getAttribute("usuarioLogueado");
    if (admin == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Proveedor> listaProveedores = (List<Proveedor>) request.getAttribute("listaProveedores");
    Proveedor proveedorAEditar = (Proveedor) request.getAttribute("proveedorAEditar");
    boolean isEditing = (proveedorAEditar != null && proveedorAEditar.getIdProveedor() > 0);
    boolean isCreating = (proveedorAEditar != null && proveedorAEditar.getIdProveedor() == 0);
    boolean showForm = isEditing || isCreating;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVCN Style - Gestión de Proveedores</title>
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
            border-left: 2px solid #555;
        }
        .catalog-submenu a {
            font-size: 0.9em;
            padding: 5px 0;
            display: block;
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
                <li><a href="dashboard.jsp">Inicio Panel</a></li>
                <li><a href="GestorProductosServlet?accion=listar">Gestión de Productos</a></li>
                <li><a href="GestorProveedoresServlet?accion=listar" class="active">Gestión de Proveedores</a></li>
                
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
            <h2 class="section-title" style="text-align: left;">Gestión de Proveedores</h2>

            <%-- Mensajes Flash y Errores --%>
            <% 
                String mensajeExitoFlash = (String) session.getAttribute("mensajeFlash");
                if (mensajeExitoFlash != null) { session.removeAttribute("mensajeFlash");
            %>
                <div class="alert alert-success"><%= mensajeExitoFlash %></div>
            <% }
                String mensajeError = (String) request.getAttribute("mensajeError");
                if (mensajeError != null) { 
            %>
                <div class="alert alert-danger"><%= mensajeError %></div>
            <% } %>

            <% if (!showForm) { %>
            <div style="margin-bottom: 20px;">
                <a href="GestorProveedoresServlet?accion=nuevo" class="button primary-button">
                    + Nuevo Proveedor
                </a>
            </div>
            <% } %>

            <% if (showForm) { %>
            <div class="card p-4 mb-4">
                <h3><%= isEditing ? "Editar Proveedor: " + proveedorAEditar.getNombreProv() : "Registrar Nuevo Proveedor" %></h3>
                <form action="GestorProveedoresServlet" method="POST">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idProveedor" value="<%= isEditing ? proveedorAEditar.getIdProveedor() : 0 %>">

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="nombreProv" class="form-label">Nombre del Proveedor / Empresa</label>
                            <input type="text" class="form-control" id="nombreProv" name="nombreProv" required
                                         value="<%= showForm && proveedorAEditar.getNombreProv() != null ? proveedorAEditar.getNombreProv() : "" %>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="contacto" class="form-label">Nombre del Contacto</label>
                            <input type="text" class="form-control" id="contacto" name="contacto"
                                         value="<%= showForm && proveedorAEditar.getContacto() != null ? proveedorAEditar.getContacto() : "" %>">
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="telefono" class="form-label">Teléfono</label>
                            <input type="tel" class="form-control" id="telefono" name="telefono"
                                         value="<%= showForm && proveedorAEditar.getTelefono() != null ? proveedorAEditar.getTelefono() : "" %>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email"
                                         value="<%= showForm && proveedorAEditar.getEmail() != null ? proveedorAEditar.getEmail() : "" %>">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-success me-2"><%= isEditing ? "Actualizar Proveedor" : "Guardar Proveedor" %></button>
                    <a href="GestorProveedoresServlet?accion=listar" class="btn btn-secondary">Cancelar</a>
                </form>
            </div>
            <% } %>

            <% if (listaProveedores != null && !listaProveedores.isEmpty()) { %>
            <h3 class="mt-4">Listado de Proveedores</h3>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre Empresa</th>
                            <th>Contacto</th>
                            <th>Teléfono</th>
                            <th>Email</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Proveedor p : listaProveedores) { %>
                        <tr>
                            <td><%= p.getIdProveedor() %></td>
                            <td><%= p.getNombreProv() %></td>
                            <td><%= p.getContacto() != null ? p.getContacto() : "N/A" %></td>
                            <td><%= p.getTelefono() != null ? p.getTelefono() : "N/A" %></td>
                            <td><%= p.getEmail() != null ? p.getEmail() : "N/A" %></td>
                            <td>
                                <a href="GestorProveedoresServlet?accion=editar&id=<%= p.getIdProveedor() %>" class="btn btn-warning btn-sm me-2">Editar</a>
                                <a href="GestorProveedoresServlet?accion=eliminar&id=<%= p.getIdProveedor() %>"
                                    onclick="return confirm('¿Estás seguro de eliminar al proveedor <%= p.getNombreProv() %>?')"
                                    class="btn btn-danger btn-sm">Eliminar</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else if (!showForm) { %>
                <p>No hay proveedores registrados en la base de datos.</p>
            <% } %>
        </main>
    </div>

    <footer class="footer">
        </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>