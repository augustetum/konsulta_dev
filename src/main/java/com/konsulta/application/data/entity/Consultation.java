package com.konsulta.application.data.entity;


import com.konsulta.application.data.entity.*;
import jakarta.persistence.*;

@Entity
@Table(name = "kon_consultations") // Name of the table in the database
public class Consultation extends AbstractEntity {

    @ManyToOne
    private Parent parent;

    @ManyToOne
    private Teacher teacher;

    @Enumerated(EnumType.STRING) // Enum should be persisted as a string
    private ConsultationStatus status;

    @ManyToOne
    private Timeslot timeslot;

    public Teacher getTeacher() {return teacher;}
    public void setTeacher(Teacher teacher) {this.teacher = teacher;}
    public ConsultationStatus getStatus() {return status;}
    public void setStatus(ConsultationStatus status) {this.status = status;}
    public Timeslot getTimeslot() {return timeslot;}
    public void setTimeslot(Timeslot timeslot) {this.timeslot = timeslot;}
    public Parent getParent() {return parent;}
    public void setParent(Parent parent) {this.parent = parent;}

}

