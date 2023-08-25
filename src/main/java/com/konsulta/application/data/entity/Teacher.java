package com.konsulta.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "kon_teachers")
public class Teacher extends Account {
    private String subject;
    private String classroom;

    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public String getClassroom() {return classroom;}
    public void setClassroom(String classroom) {this.classroom = classroom;}
}
