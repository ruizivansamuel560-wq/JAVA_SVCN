<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.svcn.modelo.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Map"%>

<%
    // ====================================================================
    // 1. Lógica JSP de configuración y obtención de atributos
    // ====================================================================
    Administrador admin = (Administrador) session.getAttribute("usuarioLogueado");
    if (admin == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Listas de productos y catálogos
    List<Producto> listaProductos = (List<Producto>) request.getAttribute("listaProductos");
    List<Proveedor> listaProveedores = (List<Proveedor>) request.getAttribute("listaProveedores");
    List<Marca> listaMarcas = (List<Marca>) request.getAttribute("listaMarcas");
    List<Talla> listaTallas = (List<Talla>) request.getAttribute("listaTallas");
    List<Color> listaColores = (List<Color>) request.getAttribute("listaColores");
    List<UnidadMedida> listaUnidades = (List<UnidadMedida>) request.getAttribute("listaUnidades");
    
    Producto productoAEditar = (Producto) request.getAttribute("productoAEditar");
    
    // Inicialización segura de listas para evitar NullPointerException
    if (listaProductos == null) listaProductos = Collections.emptyList();
    if (listaProveedores == null) listaProveedores = Collections.emptyList();
    if (listaMarcas == null) listaMarcas = Collections.emptyList();
    if (listaTallas == null) listaTallas = Collections.emptyList();
    if (listaColores == null) listaColores = Collections.emptyList();
    if (listaUnidades == null) listaUnidades = Collections.emptyList();

    // Lógica para mostrar/ocultar el formulario
    boolean isEditing = (productoAEditar != null && productoAEditar.getIdProducto() > 0);
    boolean isCreating = (productoAEditar != null && productoAEditar.getIdProducto() == 0);
    boolean showForm = isEditing || isCreating;
    
    // ====================================================================
    // 2. Inicialización de Variables para el Formulario (Incluyendo los nuevos)
    // ====================================================================
    
    // IDs seleccionados para los selects (Dropdowns)
    long idProvSeleccionado = isEditing ? productoAEditar.getIdProveedor() : 0L;
    long idMarcaSeleccionada = isEditing ? productoAEditar.getIdMarca() : 0L;
    long idTallaSeleccionada = isEditing ? productoAEditar.getIdTalla() : 0L;
    long idColorSeleccionado = isEditing ? productoAEditar.getIdColor() : 0L;
    long idUnidadSeleccionada = isEditing ? productoAEditar.getIdMedida() : 0L;
    
    // Valores de texto/numérico por defecto
    String nombreProd = isEditing && productoAEditar.getNombre() != null ? productoAEditar.getNombre() : "";
    String descripcionProd = isEditing && productoAEditar.getDescripcion() != null ? productoAEditar.getDescripcion() : "";
    String precioProd = isEditing ? String.format("%.2f", productoAEditar.getPrecioUni()) : "";
    String rutaImagenActual = isEditing && productoAEditar.getRutaImagen() != null ? productoAEditar.getRutaImagen() : "";

    // Nuevos Campos de Atributo (Deben ser inicializados)
    String materialProd = isEditing && productoAEditar.getMaterial() != null ? productoAEditar.getMaterial() : "";
    String categoriaProd = isEditing && productoAEditar.getCategoria() != null ? productoAEditar.getCategoria() : "";
    String tipoPrendaProd = isEditing && productoAEditar.getTipoPrenda() != null ? productoAEditar.getTipoPrenda() : "";
    
    // Convertir el Enum EstiloPrenda a String para el formulario
    String estiloProd = isEditing && productoAEditar.getEstilo() != null ? productoAEditar.getEstilo().name() : "";
    
    String despCuidadosProd = isEditing && productoAEditar.getDespCuidados() != null ? productoAEditar.getDespCuidados() : "";
    
    // Disponibilidad (Usamos un valor por defecto o el existente)
    String disponibilidadProd = isEditing && productoAEditar.getDisponibilidad() != null ? productoAEditar.getDisponibilidad() : "Disponible";
    
    long stockActual = 0L; // Stock solo informativo en este formulario
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVCN Style - Gestión de Productos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;700&family=Oswald:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="admin.css">
    <link rel="icon" href="img/SVCN-Style-1.jpeg" type="image/x-icon">
    <style>
        /* ... Estilos del menú (iguales) ... */
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
                <li><a href="GestorProductosServlet?accion=listar" class="active">Gestión de Productos</a></li>
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
                <li><a href="reportes-productos.jsp">Reportes</a></li>
                <li><a href="inventory.jsp">Inventario</a></li>
                <li><a href="settings.jsp">Configuración</a></li>
                <li><a href="LogoutServlet">Cerrar Sesión</a></li>
            </ul>
        </aside>

        <main class="main-content">
            <h2 class="section-title" style="text-align: left;">Gestión de Productos</h2>

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
                <a href="GestorProductosServlet?accion=nuevo" class="button primary-button">
                    + Nuevo Producto
                </a>
            </div>
            <% } %>

            <% if (showForm) { %>
            <div class="card p-4 mb-4">
                <h3><%= isEditing ? "Editar Producto: " + nombreProd : "Registrar Nuevo Producto" %></h3>
                <%-- Se asegura el enctype="multipart/form-data" para el manejo de archivos --%>
                <form action="GestorProductosServlet" method="POST" enctype="multipart/form-data"> 
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idProducto" value="<%= isEditing ? productoAEditar.getIdProducto() : 0L %>">

                    <div class="row">
                        <div class="col-md-6">
                            <%-- PRIMER BLOQUE: Datos Básicos y Media --%>
                            <div class="mb-3">
                                <label for="nombre" class="form-label">Nombre del Producto</label>
                                <input type="text" class="form-control" id="nombre" name="nombre" required
                                        value="<%= nombreProd %>">
                            </div>
                            <div class="mb-3">
                                <label for="descripcion" class="form-label">Descripción</label>
                                <textarea class="form-control" id="descripcion" name="descripcion" rows="3" required><%= descripcionProd %></textarea>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="precioUni" class="form-label">Precio Unitario ($)</label>
                                    <input type="number" step="0.01" class="form-control" id="precioUni" name="precioUni" required
                                                     value="<%= precioProd %>">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="disponibilidad" class="form-label">Disponibilidad</label>
                                    <select class="form-select" id="disponibilidad" name="disponibilidad" required>
                                        <option value="Disponible" <%= "Disponible".equals(disponibilidadProd) ? "selected" : "" %>>Disponible</option>
                                        <option value="Agotado" <%= "Agotado".equals(disponibilidadProd) ? "selected" : "" %>>Agotado</option>
                                        <option value="Baja" <%= "Baja".equals(disponibilidadProd) ? "selected" : "" %>>Stock Bajo</option>
                                    </select>
                                </div>
                            </div>
                             <div class="mb-3">
                                <label for="rutaImagen" class="form-label">Imagen del Producto</label>
                                <input type="file" class="form-control" id="rutaImagen" name="rutaImagen">
                                <% if (!rutaImagenActual.isEmpty()) { %>
                                    <small class="form-text text-muted">Imagen actual: **<%= rutaImagenActual %>**</small>
                                    <img src="<%= rutaImagenActual %>" alt="Actual" style="width: 50px; height: auto;">
                                    <input type="hidden" name="rutaImagenActual" value="<%= rutaImagenActual %>">
                                <% } %>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <%-- SEGUNDO BLOQUE: FKs y Atributos de Prenda --%>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="idProveedor" class="form-label">Proveedor</label>
                                    <select class="form-select" id="idProveedor" name="idProveedor" required>
                                        <option value="">-- Seleccionar Proveedor --</option>
                                        <% 
                                            for (Proveedor prov : listaProveedores) {
                                                String selected = prov.getIdProveedor() == idProvSeleccionado ? "selected" : "";
                                        %>
                                        <option value="<%= prov.getIdProveedor() %>" <%= selected %>><%= prov.getNombreProv() %></option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="idMarca" class="form-label">Marca</label>
                                    <select class="form-select" id="idMarca" name="idMarca" required>
                                        <option value="">-- Seleccionar Marca --</option>
                                        <% 
                                            for (Marca m : listaMarcas) {
                                                String selected = m.getIdMarca() == idMarcaSeleccionada ? "selected" : "";
                                        %>
                                        <option value="<%= m.getIdMarca() %>" <%= selected %>><%= m.getNombreMarca() %></option>
                                        <% } %>
                                    </select>
                                </div>
                            </div>
                            
                            <%-- Atributos específicos del Producto --%>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="material" class="form-label">Material</label>
                                    <input type="text" class="form-control" id="material" name="material" value="<%= materialProd %>">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="tipoPrenda" class="form-label">Tipo de Prenda</label>
                                    <input type="text" class="form-control" id="tipoPrenda" name="tipoPrenda" value="<%= tipoPrendaProd %>">
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="categoria" class="form-label">Categoría</label>
                                    <input type="text" class="form-control" id="categoria" name="categoria" value="<%= categoriaProd %>">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="estilo" class="form-label">Estilo (Enum)</label>
                                    <select class="form-select" id="estilo" name="estilo">
                                        <option value="">-- Seleccionar Estilo --</option>
                                        <% for (EstiloPrenda estiloEnum : EstiloPrenda.values()) { %>
                                            <option value="<%= estiloEnum.name() %>" <%= estiloEnum.name().equals(estiloProd) ? "selected" : "" %>>
                                                <%= estiloEnum.name() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                            </div>
                            
                            <%-- Tallas y Medidas (FKs de Catálogo) --%>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="idTalla" class="form-label">Talla</label>
                                    <select class="form-select" id="idTalla" name="idTalla" required>
                                        <option value="">-- Talla --</option>
                                        <% 
                                            for (Talla t : listaTallas) {
                                                String selected = t.getIdTalla() == idTallaSeleccionada ? "selected" : "";
                                        %>
                                        <option value="<%= t.getIdTalla() %>" <%= selected %>><%= t.getNombreTalla() %></option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="idColor" class="form-label">Color</label>
                                    <select class="form-select" id="idColor" name="idColor" required>
                                        <option value="">-- Color --</option>
                                        <% 
                                            for (Color c : listaColores) {
                                                String selected = c.getIdColor() == idColorSeleccionado ? "selected" : "";
                                        %>
                                        <option value="<%= c.getIdColor() %>" <%= selected %>><%= c.getNombreColor() %></option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="idUnidad" class="form-label">Unidad Medida</label>
                                    <select class="form-select" id="idUnidad" name="idUnidad" required>
                                        <option value="">-- Unidad --</option>
                                        <% 
                                            for (UnidadMedida u : listaUnidades) {
                                                String selected = u.getIdUnidad() == idUnidadSeleccionada ? "selected" : "";
                                        %>
                                        <option value="<%= u.getIdUnidad() %>" <%= selected %>><%= u.getNombre() %></option>
                                        <% } %>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="despCuidados" class="form-label">Descripción de Cuidados</label>
                                <textarea class="form-control" id="despCuidados" name="despCuidados" rows="3"><%= despCuidadosProd %></textarea>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4">
                        <button type="submit" class="btn btn-success me-2"><%= isEditing ? "Actualizar Producto" : "Guardar Producto" %></button>
                        <a href="GestorProductosServlet?accion=listar" class="btn btn-secondary">Cancelar</a>
                    </div>
                </form>
            </div>
            <% } %>

            <%-- TABLA DE LISTADO DE PRODUCTOS --%>
            <% if (listaProductos != null && !listaProductos.isEmpty()) { %>
            <h3 class="mt-4">Listado de Productos</h3>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Proveedor</th>
                            <th>Marca</th>
                            <th>Talla / Color</th>
                            <th>Categoría</th>
                            <th>Tipo Prenda</th>
                            <th>Estilo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Producto p : listaProductos) { %>
                        <tr>
                            <td><%= p.getIdProducto() %></td>
                            <td><%= p.getNombre() %></td>
                            <td>$<%= String.format("%.2f", p.getPrecioUni()) %></td>
                            <td>N/A (Inv. Pend.)</td> 
                            
                            <%-- Estos IDs deberían ser nombres para una mejor UX, pero se dejan como ID por simplicidad del ejemplo --%>
                            <td>ID <%= p.getIdProveedor() %></td>
                            <td>ID <%= p.getIdMarca() %></td>
                            <td>T: ID <%= p.getIdTalla() %><br>C: ID <%= p.getIdColor() %></td>
                            <td><%= p.getCategoria() != null ? p.getCategoria() : "N/A" %></td>
                            <td><%= p.getTipoPrenda() != null ? p.getTipoPrenda() : "N/A" %></td>
                            <td><%= p.getEstilo() != null ? p.getEstilo().name() : "N/A" %></td>
                            
                            <td>
                                <a href="GestorProductosServlet?accion=editar&id=<%= p.getIdProducto() %>" class="btn btn-warning btn-sm me-2">Editar</a>
                                <a href="GestorProductosServlet?accion=eliminar&id=<%= p.getIdProducto() %>"
                                    onclick="return confirm('¿Estás seguro de eliminar el producto <%= p.getNombre() %>?')"
                                    class="btn btn-danger btn-sm">Eliminar</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else if (!showForm) { %>
                <div class="alert alert-info mt-4">No hay productos registrados en la base de datos.</div>
            <% } %>
        </main>
    </div>

    <footer class="footer">
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>