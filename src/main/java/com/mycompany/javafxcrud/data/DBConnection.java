package com.mycompany.javafxcrud.data;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;

public class DBConnection {
    private Connection conectar = null;
    
    private final String host = "localhost";
    private final String puerto = "5432";
    private final String db = "postgres";
    private final String user = "postgres";
    private final String password = "31379406";
    private final String cadena = "jdbc:postgresql://" + host + ":" + puerto + "/" + db;
    
    
    private String dynamicUrl;
    private String dynamicUser;
    private String dynamicPassword;
    
    public DBConnection() {}
    
    // NUEVO MÉTODO
    public void setConnectionParams(String url, String user, String password) {
        this.dynamicUrl = url;
        this.dynamicUser = user;
        this.dynamicPassword = password;
    }
    
    public Connection estableceConexion() {
        try {
            Class.forName("org.postgresql.Driver");
            
            
            if (dynamicUrl != null && dynamicUser != null && dynamicPassword != null) {
                conectar = DriverManager.getConnection(dynamicUrl, dynamicUser, dynamicPassword);
            } else {
                conectar = DriverManager.getConnection(cadena, user, password);
            }
        } catch (Exception e) {
            showAlert("Error", "No se conectó a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conectar;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar: " + e.getMessage());
        }
    }
}