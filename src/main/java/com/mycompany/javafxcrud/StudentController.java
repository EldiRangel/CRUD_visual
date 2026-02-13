
package com.mycompany.javafxcrud;

import com.mycompany.javafxcrud.data.AppQuery;
import com.mycompany.javafxcrud.model.Student;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

public class StudentController implements Initializable {

    @FXML
    private TextField fieldFirstname;
    @FXML
    private TextField fieldMiddleName;
    @FXML
    private TextField fieldLastname;
    
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML 
    private Button btnDelete;
    
    @FXML
    private TableView<Student> tableView;
    
    @FXML
    private TableColumn<Student, Integer> colId;
    @FXML
    private TableColumn<Student, String> colFirstname;     
    @FXML
    private TableColumn<Student, String> colMiddlename;         
    @FXML
    private TableColumn<Student, String> colLastname;
     
    private Student selectedStudent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showStudents();
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
    }    
    
    @FXML
    private void addStudent(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add confirmation");
        dialog.setHeaderText("Are you sure you want to save");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        Label label = new Label("Name: " + fieldFirstname.getText() + " " + fieldLastname.getText());
        dialog.getDialogPane().setContent(label);
        
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if(result.isPresent() && result.get() == okButton){
            Student student = new Student(0, 
                fieldFirstname.getText(), 
                fieldMiddleName.getText(), 
                fieldLastname.getText());
            AppQuery query = new AppQuery();    
            query.addStudent(student);
            showStudents();
            clearFields();
        }
    }

    @FXML
    private void showStudents(){
        AppQuery query = new AppQuery();
        ObservableList<Student> list = query.getStudentList();
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colMiddlename.setCellValueFactory(new PropertyValueFactory<>("middlename"));
        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        
        tableView.setItems(list);
    }
    
    @FXML
    public void mouseClicked(MouseEvent e){
        try {
            Student student = tableView.getSelectionModel().getSelectedItem();
            if (student != null) {
                selectedStudent = new Student(
                    student.getId(),
                    student.getFirstname(),
                    student.getMiddlename(),
                    student.getLastname()
                );
fieldFirstname.setText(student.getFirstname());
                fieldMiddleName.setText(student.getMiddlename());
                fieldLastname.setText(student.getLastname());
                
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                btnSave.setDisable(true);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void updateStudent(){
        try {
            AppQuery query = new AppQuery();
            Student student = new Student(
                selectedStudent.getId(), 
                fieldFirstname.getText(), 
                fieldMiddleName.getText(), 
                fieldLastname.getText()
            );
            query.updateStudent(student);
            showStudents();
            clearFields();
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
            btnSave.setDisable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent(){
        try {
            AppQuery query = new AppQuery();
            query.deleteStudent(selectedStudent);
            showStudents();
            clearFields();
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
            btnSave.setDisable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } 
    
    private void clearFields(){
        fieldFirstname.setText("");
        fieldMiddleName.setText("");
        fieldLastname.setText("");
        selectedStudent = null;
    }

    @FXML
    private void clickNew(){
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
        clearFields();
    }
}