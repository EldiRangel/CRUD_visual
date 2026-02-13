package com.mycompany.javafxcrud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import java.sql.Connection;
import java.util.Optional;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Connection dbConnection;
    private static String selectedTable;

    @Override
    public void start(Stage stage) throws IOException {
        // USANDO TU connection.fxml ORIGINAL
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connection.fxml"));
        DialogPane dialogPane = loader.load();
        ConnectionController connectionController = loader.getController();
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Conexión a PostgreSQL");
        dialog.setDialogPane(dialogPane);
        dialog.setResizable(true);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbConnection = connectionController.getConnection();
            selectedTable = connectionController.getSelectedTable();
            
            if (dbConnection != null && selectedTable != null && !selectedTable.isEmpty()) {
                scene = new Scene(loadFXML("student"), 640, 480);
                stage.setScene(scene);
                stage.show();
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Connection getConnection() {
        return dbConnection;
    }
    
    public static String getSelectedTable() {
        return selectedTable;
    }

    public static void main(String[] args) {
        launch();
    }
}