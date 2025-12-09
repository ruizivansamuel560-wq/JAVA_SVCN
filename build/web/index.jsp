<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.svcn.modelo.Producto"%>
<%@page import="com.svcn.dao.ProductoDAO"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.SQLException"%> 
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVCN Style - Moda Oversized y Moderna</title>
    <link rel="stylesheet" href="style.css"> 
    <link rel="icon" href="img/SVCN-Style-1.jpeg" type="image/x-icon">
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
                    <li><a href="index.jsp" class="nav-link current">Inicio</a></li>
                    <li><a href="products.jsp" class="nav-link">Productos</a></li>
                    <li><a href="novedades.jsp" class="nav-link">Novedades</a></li>
                    <li><a href="nosotros.jsp" class="nav-link">Acerca de</a></li>
                    <li><a href="login.jsp" class="nav-link nav-icon">🧍‍♂️</a></li>
                    <li><a href="cart.jsp" class="nav-link nav-icon">🛒</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <section class="hero-section">
        <div class="container hero-content">
            <div class="hero-text">
                <h1>Tu Estilo. Tu Esencia.</h1>
                <p class="hero-subtitle">Explora nuestra colección de moda oversized que redefine la comodidad y el estilo moderno para jóvenes.</p>
                <a href="products.jsp" class="button primary-button">Comprar Ahora</a>
            </div>
            <div class="hero-image-container">
                <img src="img/SVCN-Style-2.jpeg" alt="Modelo con ropa oversized SVCN Style" class="hero-image">
            </div>
        </div>
    </section>

    
    <section class="featured-products-section">
        <div class="container">
            <h2 class="section-title">Nuestros Favoritos</h2>
            <div class="product-grid">

                <%
                    // 1. Instanciación del DAO
                    ProductoDAO dao = new ProductoDAO();
                    // 2. Obtención de la lista (Aquí puede ocurrir el error SQL)
                    List<Producto> productos = dao.listarProductos();
                    
                    if (productos.isEmpty()) {
                        out.println("<p style='text-align:center;'>No se encontraron productos. Revisa la consola de Tomcat para ver el error de SQL exacto.</p>");
                    }
                    
                    // 3. Iteración y renderizado
                    for (Producto p : productos) {
                %>
                
                <div class="product-card">
                    <img src="<%= p.getRutaImagen() %>" alt="<%= p.getNombre() %>" class="product-card-img">
                    <div class="product-card-body">
                        <h5 class="product-card-title"><%= p.getNombre() %></h5>
                        <p class="product-card-text"><%= p.getDescripcion() %></p>
                        <span class="product-price">$<%= String.format("%.2f", p.getPrecioUni()) %></span>
                        <a href="product_detail.jsp?id=<%= p.getIdProducto() %>" class="button secondary-button">Ver Producto</a>
                    </div>
                </div>
                
                <%
                    } 
                %>
                
            </div>
        </div>
    </section>

    <footer class="footer">
        <div class="container footer-content">
            <div class="footer-column">
                <h5 class="footer-title">SVCN Style</h5>
                <p>Moda contemporánea, diseño exclusivo y calidad.</p>
                <p>&copy; 2025 SVCN Style. Todos los derechos reservados.</p>
            </div>
            <div class="footer-column">
                <h5 class="footer-title">Enlaces Rápidos</h5>
                <ul>
                    <li><a href="nosotros.jsp" class="footer-link">Sobre Nosotros</a></li>
                    <li><a href="faq.jsp" class="footer-link">Preguntas Frecuentes</a></li>
                    <li><a href="politica.jsp" class="footer-link">Política de Privacidad</a></li>
                    <li><a href="terminos.jsp" class="footer-link">Términos y Condiciones</a></li>
                </ul>
            </div>
            <div class="footer-column">
                <h5 class="footer-title">Síguenos</h5>
                <ul class="social-icons">
                    <li><a href="#" class="footer-link">Facebook</a></li>
                    <li><a href="#" class="footer-link">Instagram</a></li>
                    <li><a href="#" class="footer-link">TikTok</a></li>
                </ul>
            </div>
        </div>
    </footer>

</body>
</html>