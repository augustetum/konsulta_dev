package com.konsulta.application.data.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "kon_teachers")
public class Teacher extends Account {
    private String subject;
    private String classroom;

    @OneToMany
    private List<Timeslot> timeslots;

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }

    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public String getClassroom() {return classroom;}
    public void setClassroom(String classroom) {this.classroom = classroom;}
}
