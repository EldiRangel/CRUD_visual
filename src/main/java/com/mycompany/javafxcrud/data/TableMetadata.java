package com.mycompany.javafxcrud.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableMetadata {
    private String tableName;
    private List<ColumnInfo> columns;
    private String primaryKey;
    
    public static class ColumnInfo {
        private String name;
        private String type;
        private boolean isPrimaryKey;
        private boolean isNullable;
        private int size;
        
        public ColumnInfo(String name, String type, boolean isPrimaryKey, boolean isNullable, int size) {
            this.name = name;
            this.type = type;
            this.isPrimaryKey = isPrimaryKey;
            this.isNullable = isNullable;
            this.size = size;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public boolean isPrimaryKey() { return isPrimaryKey; }
        public boolean isNullable() { return isNullable; }
        public int getSize() { return size; }
    }
    
    public TableMetadata(Connection conn, String tableName) throws SQLException {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        loadMetadata(conn);
    }
    
    private void loadMetadata(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        ResultSet rs = metaData.getColumns(null, "public", tableName, "%");
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            String columnType = rs.getString("TYPE_NAME");
            boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
            int size = rs.getInt("COLUMN_SIZE");
            
            columns.add(new ColumnInfo(columnName, columnType, false, isNullable, size));
        }
        rs.close();
        
        rs = metaData.getPrimaryKeys(null, "public", tableName);
        if (rs.next()) {
            primaryKey = rs.getString("COLUMN_NAME");
            for (int i = 0; i < columns.size(); i++) {
                ColumnInfo col = columns.get(i);
                if (col.getName().equals(primaryKey)) {
                    columns.set(i, new ColumnInfo(
                        col.getName(), col.getType(), true, col.isNullable(), col.getSize()
                    ));
                    break;
                }
            }
        }
        rs.close();
    }
    
    public String getTableName() { return tableName; }
    public List<ColumnInfo> getColumns() { return columns; }
    public String getPrimaryKey() { return primaryKey; }
    
    public String generateInsertSQL() {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder(") VALUES (");
        
        boolean first = true;
        for (ColumnInfo col : columns) {
            if (col.isPrimaryKey()) continue;
            
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(col.getName());
            values.append("?");
            first = false;
        }
        
        sql.append(values.toString() + ")");
        return sql.toString();
    }
    
    public String generateUpdateSQL() {
        if (primaryKey == null) return null;
        
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        boolean first = true;
        
        for (ColumnInfo col : columns) {
            if (col.isPrimaryKey()) continue;
            
            if (!first) sql.append(", ");
            sql.append(col.getName()).append(" = ?");
            first = false;
        }
        
        sql.append(" WHERE ").append(primaryKey).append(" = ?");
        return sql.toString();
    }
    
    public String generateDeleteSQL() {
        return primaryKey != null ? "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?" : null;
    }
    
    public String generateSelectSQL() {
        return "SELECT * FROM " + tableName;
    }
}