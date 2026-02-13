module com.mycompany.javafxcrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.base;
    requires java.sql;

    opens com.mycompany.javafxcrud to javafx.fxml;
    exports com.mycompany.javafxcrud;
     exports com.mycompany.javafxcrud.data;
      exports com.mycompany.javafxcrud.model;
}
