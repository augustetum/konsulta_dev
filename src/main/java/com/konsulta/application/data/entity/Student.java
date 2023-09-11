package com.konsulta.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "kon_students")
public class Student extends AbstractEntity implements Serializable {

    private String studentName;
    private String studentSurname;
    private String studentClass;

    public String getStudentName() {return studentName; }
    public void setStudentName(String studentName){this.studentName = studentName;}
    public String getStudentSurname() {return studentSurname; }
    public void setStudentSurname(String studentSurname){this.studentSurname = studentSurname;}
    public String getStudentClass() {return studentClass; }
    public void setStudentClass(String studentClass){this.studentClass = studentClass;}
}
