package com.mycompany.javafxcrud.data;

import java.sql.*;
import java.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DBUser {

    private final Connection connection;
    private String table;

    private List<String> columns = new ArrayList<>();
    private List<Integer> types = new ArrayList<>();
    private List<String> columnsWithoutPK = new ArrayList<>();

    public DBUser(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    private void loadMetadata() throws Exception {
        columns.clear();
        types.clear();
        columnsWithoutPK.clear();

        String sql = "SELECT * FROM " + table + " LIMIT 1";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();

        int count = meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            columns.add(meta.getColumnName(i));
            types.add(meta.getColumnType(i));
        }

        for (int i = 1; i < columns.size(); i++) {
            columnsWithoutPK.add(columns.get(i));
        }

        rs.close();
        st.close();
    }

    public void buildForm(VBox container, Map<String, TextField> fields) {
        try {
            loadMetadata();
            container.getChildren().clear();
            fields.clear();

            for (String col : columns) {
                Label lbl = new Label(col + ":");
                TextField txt = new TextField();
                txt.setPromptText(col);
                container.getChildren().addAll(lbl, txt);
                fields.put(col, txt);
            }

        } catch (Exception e) {
            showAlert("ERROR", "Error building form: " + e.toString());
        }
    }

    public void showGenericTable(TableView<Object[]> tableView) {
        try {
            loadMetadata();

            String sql = "SELECT * FROM " + table;
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);

            tableView.getColumns().clear();
            tableView.getItems().clear();

            int count = columns.size();

            for (int i = 0; i < count; i++) {
                final int index = i;
                TableColumn<Object[], String> col = new TableColumn<>(columns.get(i));
                col.setCellValueFactory(data ->
                        new SimpleStringProperty(
                                data.getValue()[index] != null ? data.getValue()[index].toString() : ""
                        )
                );
                tableView.getColumns().add(col);
            }

            while (rs.next()) {
                Object[] row = new Object[count];
                for (int i = 0; i < count; i++) {
                    row[i] = rs.getObject(columns.get(i));
                }
                tableView.getItems().add(row);
            }

            rs.close();
            st.close();

        } catch (Exception e) {
            showAlert("ERROR", "Error showing table: " + e.toString());
        }
    }

    public void selectRow(TableView<Object[]> tableView, Map<String, TextField> fields) {
        int row = tableView.getSelectionModel().getSelectedIndex();
        if (row < 0) return;

        Object[] data = tableView.getItems().get(row);

        for (int i = 0; i < columns.size(); i++) {
            fields.get(columns.get(i)).setText(
                    data[i] != null ? data[i].toString() : ""
            );
        }
    }

    public void insertRecord(Map<String, TextField> fields) {
        try {
            loadMetadata();

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(table).append(" (");

            for (int i = 1; i < columns.size(); i++) {
                sb.append(columns.get(i));
                if (i < columns.size() - 1) sb.append(", ");
            }

            sb.append(") VALUES (");

            for (int i = 1; i < columns.size(); i++) {
                sb.append("?");
                if (i < columns.size() - 1) sb.append(", ");
            }

            sb.append(")");

            PreparedStatement ps = connection.prepareStatement(sb.toString());

            int index = 1;
            for (int i = 1; i < columns.size(); i++) {
                setValue(ps, index++, types.get(i), fields.get(columns.get(i)).getText());
            }

            ps.executeUpdate();
            ps.close();

            showAlert("Information", "Record inserted successfully");

        } catch (Exception e) {
            showAlert("ERROR", "Error inserting: " + e.toString());
        }
    }

    public void updateRecord(Map<String, TextField> fields) {
        try {
            loadMetadata();

            String pkCol = columns.get(0);
            String pkVal = fields.get(pkCol).getText();

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(table).append(" SET ");

            for (int i = 1; i < columns.size(); i++) {
                sb.append(columns.get(i)).append("=?");
                if (i < columns.size() - 1) sb.append(", ");
            }

            sb.append(" WHERE ").append(pkCol).append("=?");

            PreparedStatement ps = connection.prepareStatement(sb.toString());

            int index = 1;
            for (int i = 1; i < columns.size(); i++) {
                setValue(ps, index++, types.get(i), fields.get(columns.get(i)).getText());
            }

            setValue(ps, index, types.get(0), pkVal);

            ps.executeUpdate();
            ps.close();

            showAlert("Information", "Record updated successfully");

        } catch (Exception e) {
            showAlert("ERROR", "Error updating: " + e.toString());
        }
    }

    public void deleteRecord(Map<String, TextField> fields) {
        try {
            loadMetadata();

            String pkCol = columns.get(0);
            String pkVal = fields.get(pkCol).getText();

            String sql = "DELETE FROM " + table + " WHERE " + pkCol + "=?";

            PreparedStatement ps = connection.prepareStatement(sql);
            setValue(ps, 1, types.get(0), pkVal);
            ps.executeUpdate();
            ps.close();

            showAlert("Information", "Record deleted successfully");

        } catch (Exception e) {
            showAlert("ERROR", "Error deleting: " + e.toString());
        }
    }

    private void setValue(PreparedStatement ps, int index, int type, String value) throws Exception {
        if (value == null || value.isEmpty()) {
            ps.setNull(index, type);
            return;
        }

        switch (type) {
            case Types.INTEGER:
                ps.setInt(index, Integer.parseInt(value));
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                ps.setBigDecimal(index, new java.math.BigDecimal(value));
                break;

            default:
                ps.setString(index, value);
                break;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}