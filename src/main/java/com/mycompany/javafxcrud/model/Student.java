/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxcrud.model;

/**
 *
 * @author Eldi
 */
public class Student {
    private Integer id;
    private String firstname; 
    private String middlename;
    private String lastname;
    
    public Student(Integer id, String firstname, String middlename, String lastname){ 
        this.id = id;
        this.firstname = firstname; 
        this.middlename = middlename;
        this.lastname = lastname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {  
        return firstname;
    }

    public void setFirstname(String firstname) {  
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}