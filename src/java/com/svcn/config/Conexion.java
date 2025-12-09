package com.svcn.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // 🚨 REVISA ESTOS VALORES 🚨
    private static final String URL = "jdbc:mysql://localhost:3306/svcn_db"; // Usa el nombre de tu DB
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; // Vacío si usas XAMPP por defecto

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            // Este es el log que verás si hay un fallo de conexión
            System.err.println("ERROR DE CONEXIÓN EN GETCONNECTION:");
            e.printStackTrace();
        }
        return conn; // Retornará null si hay error (causando el NullPointerException)
    }
}