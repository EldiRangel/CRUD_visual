
package com.mycompany.javafxcrud.data;

import com.mycompany.javafxcrud.model.Student;
import com.mycompany.javafxcrud.model.GenericRecord;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppQuery {
    private DBConnection c = new DBConnection();
    
    public void addStudent(Student student) {
        try {
            PreparedStatement ps = c.estableceConexion().prepareStatement(
                "INSERT INTO student(firstname, middlename, lastname) VALUES (?, ?, ?)"
            );
            
            ps.setString(1, student.getFirstname());
            ps.setString(2, student.getMiddlename());
            ps.setString(3, student.getLastname());
            
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ObservableList<Student> getStudentList() {
        ObservableList<Student> studentList = FXCollections.observableArrayList();
        try {
            String query = "SELECT id, firstname, middlename, lastname FROM student ORDER BY lastname ASC";
            Statement st = c.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(query);
            
            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("id"), 
                    rs.getString("firstname"), 
                    rs.getString("middlename"), 
                    rs.getString("lastname")
                );
                studentList.add(s);
            }
            
            rs.close();
            st.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentList;
    }
    
    public void updateStudent(Student student) {
        try {
            PreparedStatement ps = c.estableceConexion().prepareStatement(
                "UPDATE student SET firstname = ?, middlename = ?, lastname = ? WHERE id = ?"
            );
            
            ps.setString(1, student.getFirstname());
            ps.setString(2, student.getMiddlename());
            ps.setString(3, student.getLastname());
            ps.setInt(4, student.getId());
            
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void deleteStudent(Student student) {
        try {
            PreparedStatement ps = c.estableceConexion().prepareStatement(
                "DELETE FROM student WHERE id = ?"
            );
            
            ps.setInt(1, student.getId());
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
  
    public TableMetadata getTableMetadata(String tableName) {
        try {
            Connection conn = c.estableceConexion();
            return new TableMetadata(conn, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ObservableList<GenericRecord> getDynamicRecords(String tableName) {
        ObservableList<GenericRecord> records = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM " + tableName;
            Statement st = c.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

while (rs.next()) {
                GenericRecord record = new GenericRecord();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    record.setValue(columnName, value);
                }
                records.add(record);
            }
            
            rs.close();
            st.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public void addDynamicRecord(GenericRecord record, TableMetadata metadata) {
        try {
            String sql = metadata.generateInsertSQL();
            PreparedStatement ps = c.estableceConexion().prepareStatement(sql);
            
            int paramIndex = 1;
            for (TableMetadata.ColumnInfo col : metadata.getColumns()) {
                if (col.isPrimaryKey()) continue;
                ps.setObject(paramIndex++, record.getValue(col.getName()));
            }
            
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateDynamicRecord(GenericRecord record, TableMetadata metadata) {
        try {
            String sql = metadata.generateUpdateSQL();
            if (sql == null) return;
            
            PreparedStatement ps = c.estableceConexion().prepareStatement(sql);
            
            int paramIndex = 1;
            for (TableMetadata.ColumnInfo col : metadata.getColumns()) {
                if (col.isPrimaryKey()) continue;
                ps.setObject(paramIndex++, record.getValue(col.getName()));
            }
            ps.setObject(paramIndex, record.getValue(metadata.getPrimaryKey()));
            
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteDynamicRecord(GenericRecord record, TableMetadata metadata) {
        try {
            String sql = metadata.generateDeleteSQL();
            if (sql == null) return;
            
            PreparedStatement ps = c.estableceConexion().prepareStatement(sql);
            ps.setObject(1, record.getValue(metadata.getPrimaryKey()));
            
            ps.execute();
            ps.close();
            c.cerrarConexion();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}