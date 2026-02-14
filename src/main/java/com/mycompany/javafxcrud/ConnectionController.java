package com.mycompany.javafxcrud;

import com.mycompany.javafxcrud.data.DBConnection;
import com.mycompany.javafxcrud.data.DBUser;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ConnectionController implements Initializable {

    private Connection connection;
    private DBUser dao;
    private Map<String, TextField> fields = new HashMap<>();

    @FXML private TextField txthost;
    @FXML private TextField txtport;
    @FXML private TextField txtdb;
    @FXML private TextField txtuser;
    @FXML private PasswordField txtpassword;
    @FXML private TextField txttable;

    @FXML private VBox formContainer;
    @FXML private TableView<Object[]> tbData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void connectDB(ActionEvent event) {
        String host = txthost.getText();
        String port = txtport.getText();
        String db = txtdb.getText();
        String user = txtuser.getText();
        String password = txtpassword.getText();
        String table = txttable.getText();

        DBConnection cc = new DBConnection();
        connection = cc.establishConnection(host, port, db, user, password);

        if (connection != null) {
            dao = new DBUser(connection, table);

            dao.buildForm(formContainer, fields);
            dao.showGenericTable(tbData);
        }
    }

    @FXML
    private void save(ActionEvent event) {
        if (dao == null) return;
        dao.insertRecord(fields);
        dao.showGenericTable(tbData);
    }

    @FXML
    private void update(ActionEvent event) {
        if (dao == null) return;
        dao.updateRecord(fields);
        dao.showGenericTable(tbData);
    }

    @FXML
    private void delete(ActionEvent event) {
        if (dao == null) return;
        dao.deleteRecord(fields);
        dao.showGenericTable(tbData);
    }

    @FXML
    private void selectRow(MouseEvent event) {
        if (dao == null) return;
        dao.selectRow(tbData, fields);
    }
}