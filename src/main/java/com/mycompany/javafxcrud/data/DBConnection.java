package com.mycompany.javafxcrud.data;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;

public class DBConnection {

    private Connection connect = null;

    public Connection establishConnection(String host, String port, String db, String user, String password) {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        try {
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection(url, user, password);
            showAlert("Message", "Successful connection to database: " + db);
        } catch (Exception e) {
            showAlert("Message", "Error: " + e.toString());
        }
        return connect;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void closeConnection() {
        try {
            if (connect != null && !connect.isClosed()) {
                connect.close();
                showAlert("Message", "Connection closed");
            }
        } catch (Exception e) {
            showAlert("Message", "Error closing connection: " + e.toString());
        }
    }
}