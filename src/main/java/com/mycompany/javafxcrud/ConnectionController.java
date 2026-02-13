package com.mycompany.javafxcrud;

import com.mycompany.javafxcrud.data.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ConnectionController implements Initializable {
    
    @FXML private TextField txtHost;
    @FXML private TextField txtPort;
    @FXML private TextField txtDatabase;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbTables;
    @FXML private Button btnLoadTables;
    @FXML private ProgressIndicator progressIndicator;
    
    private DBConnection dbConnection;
    private Connection connection;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbConnection = new DBConnection();
        cmbTables.setDisable(true);
        
        txtHost.setText("localhost");
        txtPort.setText("5432");
        txtDatabase.setText("postgres");
        txtUser.setText("postgres");
    }
    
    @FXML
    private void loadTables() {
        try {
            progressIndicator.setVisible(true);
            btnLoadTables.setDisable(true);
            
            String host = txtHost.getText();
            String port = txtPort.getText();
            String database = txtDatabase.getText();
            String user = txtUser.getText();
            String password = txtPassword.getText();
            
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            
            dbConnection.setConnectionParams(url, user, password);
            connection = dbConnection.estableceConexion();
            
            if (connection != null) {
                ObservableList<String> tables = FXCollections.observableArrayList();
                DatabaseMetaData metaData = connection.getMetaData();
                
                ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
                
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
                rs.close();
                
                cmbTables.setItems(tables);
                cmbTables.setDisable(false);
                
                showAlert("Éxito", "Conectado a PostgreSQL. " + tables.size() + " tablas encontradas.", Alert.AlertType.INFORMATION);
            }
            
        } catch (Exception e) {
            showAlert("Error", "No se pudo conectar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            progressIndicator.setVisible(false);
            btnLoadTables.setDisable(false);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public String getSelectedTable() {
        return cmbTables.getValue();
    }
}