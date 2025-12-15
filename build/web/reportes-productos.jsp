<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.svcn.modelo.Producto"%>
<%@page import="com.svcn.modelo.EstiloPrenda"%>
<%@page import="com.svcn.modelo.Administrador"%>
<%@page import="java.util.List"%>
<%
    // Verificación de seguridad (el AuthFilter ya lo hace, pero es buena práctica)
    Administrador admin = (Administrador) session.getAttribute("usuarioLogueado");
    if (admin == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Obtener los resultados y los filtros guardados del Servlet
    List<Producto> resultados = (List<Producto>) request.getAttribute("resultadosReporte");
    String filtroNombre = (String) request.getAttribute("filtroNombre");
    String filtroDescripcion = (String) request.getAttribute("filtroDescripcion");
    String filtroPrecioMin = (String) request.getAttribute("filtroPrecioMin");
    String filtroPrecioMax = (String) request.getAttribute("filtroPrecioMax");

    // --- INICIO: OBTENER NUEVOS FILTROS DEL SERVLET ---
    String filtroMaterial = (String) request.getAttribute("filtroMaterial");
    String filtroCategoria = (String) request.getAttribute("filtroCategoria");
    String filtroEstilo = (String) request.getAttribute("filtroEstilo");
    String filtroDisponibilidad = (String) request.getAttribute("filtroDisponibilidad");
    // --- FIN: OBTENER NUEVOS FILTROS DEL SERVLET ---

    // Si no hay resultados (primera carga), inicializar la lista vacía
    if (resultados == null) {
        resultados = java.util.Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVCN Style - Reporte de Productos</title>
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
                <li><a href="ReportesServlet" class="active">Reportes</a></li>
                <li><a href="inventory.jsp">Inventario</a></li>
                <li><a href="settings.jsp">Configuración</a></li>
                <li><a href="LogoutServlet">Cerrar Sesión</a></li>
            </ul>
        </aside>

        <main class="main-content">
            <h2 class="section-title" style="text-align: left;">Generación de Reportes (Productos)</h2>
            <p>Utiliza los filtros multicriterio para encontrar la información deseada.</p>

            <% 
                String mensajeError = (String) request.getAttribute("mensajeError");
                if (mensajeError != null) {
            %>
                <div class="alert alert-danger"><%= mensajeError %></div>
            <%
                }
            %>

            <div class="card p-4 mb-4">
                <h3>Filtros de Búsqueda</h3>
                <form action="ReportesServlet" method="POST" class="row g-3">
                    
                    <div class="col-md-6">
                        <label for="nombre" class="form-label">Nombre de Producto (Parcial)</label>
                        <input type="text" class="form-control" id="nombre" name="nombre" 
                               value="<%= filtroNombre != null ? filtroNombre : "" %>">
                    </div>
                    
                    <div class="col-md-6">
                        <label for="descripcion" class="form-label">Descripción (Palabra clave)</label>
                        <input type="text" class="form-control" id="descripcion" name="descripcion" 
                               value="<%= filtroDescripcion != null ? filtroDescripcion : "" %>">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="precioMin" class="form-label">Precio Mínimo ($)</label>
                        <input type="number" step="0.01" class="form-control" id="precioMin" name="precioMin" 
                               value="<%= filtroPrecioMin != null ? filtroPrecioMin : "" %>">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="precioMax" class="form-label">Precio Máximo ($)</label>
                        <input type="number" step="0.01" class="form-control" id="precioMax" name="precioMax" 
                               value="<%= filtroPrecioMax != null ? filtroPrecioMax : "" %>">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="material" class="form-label">Material (Parcial)</label>
                        <input type="text" class="form-control" id="material" name="material" 
                               value="<%= filtroMaterial != null ? filtroMaterial : "" %>">
                    </div>

                    <div class="col-md-3">
                        <label for="categoria" class="form-label">Categoría (Parcial)</label>
                        <input type="text" class="form-control" id="categoria" name="categoria" 
                               value="<%= filtroCategoria != null ? filtroCategoria : "" %>">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="estilo" class="form-label">Estilo</label>
                        <select class="form-select" id="estilo" name="estilo">
                            <option value="">Cualquiera</option>
                            <% 
                                // Llena el select usando el Enum de Java
                                for (com.svcn.modelo.EstiloPrenda estilo : com.svcn.modelo.EstiloPrenda.values()) {
                                    String selected = (filtroEstilo != null && filtroEstilo.equals(estilo.name())) ? "selected" : "";
                            %>
                                    <option value="<%= estilo.name() %>" <%= selected %>><%= estilo.name().replace("_", " ") %></option>
                            <% 
                                } 
                            %>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="disponibilidad" class="form-label">Disponibilidad</label>
                        <select class="form-select" id="disponibilidad" name="disponibilidad">
                             <option value="">Cualquiera</option>
                             <option value="ALTA" <%= "ALTA".equals(filtroDisponibilidad) ? "selected" : "" %>>Alta</option>
                             <option value="MEDIA" <%= "MEDIA".equals(filtroDisponibilidad) ? "selected" : "" %>>Media</option>
                             <option value="BAJA" <%= "BAJA".equals(filtroDisponibilidad) ? "selected" : "" %>>Baja</option>
                        </select>
                    </div>
                    <div class="col-12 mt-4">
                        <button type="submit" class="button primary-button">Generar Reporte</button>
                    </div>
                </form>
            </div>

            <h3 class="mt-4">Resultados de la Consulta (<%= resultados.size() %> Productos)</h3>
            
            <% if (!resultados.isEmpty()) { %>
            <div class="table-responsive">
                <table class="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Material</th> 
                            <th>Categoría</th> 
                            <th>Estilo</th> 
                            <th>Disponibilidad</th> 
                            <th>Descripción</th>
                            <th>Imagen</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Producto p : resultados) { %>
                        <tr>
                            <td><%= p.getIdProducto() %></td>
                            <td><%= p.getNombre() %></td>
                            <td>$<%= String.format("%.2f", p.getPrecioUni()) %></td>
                            <td><%= p.getMaterial() != null ? p.getMaterial() : "N/A" %></td>
                            <td><%= p.getCategoria() != null ? p.getCategoria() : "N/A" %></td>
                            <td><%= p.getEstilo() != null ? p.getEstilo().name().replace("_", " ") : "N/A" %></td>
                            <td>
                                 <%
                                     String disp = p.getDisponibilidad() != null ? p.getDisponibilidad() : "N/A";
                                     String badgeClass = disp.equals("ALTA") ? "bg-success" : (disp.equals("MEDIA") ? "bg-warning text-dark" : "bg-danger");
                                 %>
                                 <span class="badge <%= badgeClass %>"><%= disp %></span>
                            </td>
                            <td><%= p.getDescripcion().length() > 50 ? p.getDescripcion().substring(0, 50) + "..." : p.getDescripcion() %></td>
                            <td><img src="<%= p.getRutaImagen() %>" alt="Img" style="width: 50px; height: auto;"></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
                <div class="alert alert-info">
                    No se encontraron productos que coincidan con los criterios de búsqueda. Intente con filtros más amplios.
                </div>
            <% } %>

        </main>
    </div>

    <footer class="footer">
        </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>