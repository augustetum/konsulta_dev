package com.konsulta.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "kon_students")
public class Student extends AbstractEntity{

    private String studentName;
    private String studentSurname;
    private String studentClass;
}
